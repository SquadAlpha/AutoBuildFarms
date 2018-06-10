package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.Registry;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

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
            TextComponent tc = new TextComponent("Wrong subcommand ");
            tc.setColor(ChatColor.RED);
            ComponentBuilder cpb = new ComponentBuilder(tc);
            tc = new TextComponent("Possible options:");
            tc.setColor(ChatColor.WHITE);
            cpb.append(tc);
            TextComponent ntc = new TextComponent("");
            ntc.setColor(ChatColor.YELLOW);
            this.suboptions.getObjects().forEach(o -> ntc.setText(o.getName() + "," + ntc.getText()));
            cpb.append(ntc);
            sender.spigot().sendMessage(cpb.create());
            return true;
        } else {
            return opt.go(sender, command, label, args);
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
        public final String name;
        public final Function<onCommandArgs, Boolean> exec;

        public final boolean go(CommandSender sender, Command cmd, String label, String[] args) {
            return this.exec.apply(new onCommandArgs(sender, cmd, label, args));
        }
    }

}
