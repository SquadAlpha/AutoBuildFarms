package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.Registry;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.commandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class CommandWithSubCommands extends ABFCommand {
    @Getter(AccessLevel.PROTECTED)
    private final Registry<SubOption> subOptions;

    protected CommandWithSubCommands(String name, AutoBuildFarms plugin) {
        super(name, plugin);
        this.subOptions = new Registry<>();
    }

    protected final void registerSubOption(SubOption opt) {
        this.subOptions.add(opt);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 0) {
            return this.mainCommand(sender, command, label, args);
        }
        SubOption opt = this.getSubOptions().searchFirst(args[0]);
        if (opt == null) {
            ChatBuilder builder = new ChatBuilder(sender)
                    .append(ChatColor.RED, "Wrong sub-command")
                    .append(ChatColor.WHITE, "Possible options:");
            this.subOptions.getObjects().forEach(o -> builder
                    .append(ChatColor.YELLOW, o.getName())
                    .append(ChatColor.WHITE, ", "));
            builder.send();
            return true;
        } else {
            if (sender.hasPermission(opt.getPermission())) {
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
        ArrayList<String> suggestions = new ArrayList<>();
        if (args.length <= 1) {
            String search;
            if (args.length == 1) {
                search = args[0];
            } else {
                search = "";
            }
            this.getSubOptions().forEach(so -> {
                if (so.getName().startsWith(search)) {
                    suggestions.add(so.getName());
                }
            });
        } else {
            SubOption opt = this.getSubOptions().searchFirst(args[0]);
            if (opt != null) {
                if (sender.hasPermission(opt.getPermission())) {
                    return opt.goTabComplete(sender, command, alias, args);
                }
            }
        }
        return suggestions;
    }

    @RequiredArgsConstructor
    @Getter
    public static class SubOption implements RegistryObject {
        private final CommandWithSubCommands parent;
        private final String name;
        private final Function<commandArgs, Boolean> exec;
        private final Function<commandArgs, List<String>> tabComplete;

        public final boolean go(CommandSender sender, Command cmd, String label, String[] args) {
            return this.exec.apply(new commandArgs(sender, (PluginCommand) cmd, label, args));
        }

        public final String getPermission() {
            return this.getParent().getCommand().getPermission() + "." + this.getName();
        }

        public List<String> goTabComplete(CommandSender sender, Command command, String alias, String[] args) {
            return this.tabComplete.apply(new commandArgs(sender, (PluginCommand) command, alias, args));
        }
    }

}
