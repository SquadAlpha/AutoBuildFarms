package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class CommandwithSubcommands extends ABFCommand {
    protected CommandwithSubcommands(String name, AutoBuildFarms plugin) {
        super(name, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return this.mainCommand(sender, command, label, args);
        }
        return false;
    }

    protected abstract boolean mainCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
