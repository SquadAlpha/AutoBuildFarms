package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.Registry;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.List;
import java.util.function.Function;

public abstract class CommandwithSubcommands extends ABFCommand {
    @Getter(AccessLevel.PROTECTED)
    private final Registry<subOption> suboptions;

    protected CommandwithSubcommands(String name, AutoBuildFarms plugin) {
        super(name, plugin);
        this.suboptions = new Registry<>();
    }

    protected final void registerSubOption(subOption opt) {
        this.suboptions.add(opt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            return this.mainCommand(sender, command, label, args);
        }
        subOption opt = this.getSuboptions().searchFirst(args[0]);
        if (opt == null) {
            ChatBuilder builder = new ChatBuilder(sender)
                    .append(ChatColor.RED, "Wrong sub-command")
                    .append(ChatColor.WHITE, "Possible options:");
            this.suboptions.getObjects().forEach(o -> builder
                    .append(ChatColor.YELLOW, o.getName())
                    .append(ChatColor.WHITE, ", "));
            builder.send();
            return true;
        } else {
            if (sender.hasPermission(this.getCommand().getPermission() + "." + opt.getName())) {
                return opt.go(sender, command, label, args);
            } else {
                new ChatBuilder(sender)
                        .append(ChatColor.RED, "You don't have permission:")
                        .append(ChatColor.AQUA, opt.getPermission())
                        .append(ChatColor.RED, " to execute this sub-command")
                        .send();
                return true;
            }
        }
    }

    protected abstract boolean mainCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @RequiredArgsConstructor
    @Getter
    public static class subOption implements RegistryObject {
        private final CommandwithSubcommands parent;
        private final String name;
        private final Function<onCommandArgs, Boolean> exec;

        public final boolean go(CommandSender sender, Command cmd, String label, String[] args) {
            return this.exec.apply(new onCommandArgs(sender, (PluginCommand) cmd, label, args));
        }

        public final String getPermission() {
            return this.getParent().getCommand().getPermission() + "." + this.getName();
        }
    }

}
