package com.github.SquadAlpha.AutoBuildFarms.commands;


import com.github.SquadAlpha.AutoBuildFarms.Reference;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;

public abstract class ABFCommand extends Command implements CommandExecutor, TabCompleter{


    protected ABFCommand(String name, String description, String usageMessage, String... aliases){
        super(name, description, usageMessage, Arrays.asList(aliases));
        this.setPermission(Reference.plugin.getName().toLowerCase() + "." + name);
        this.setPermissionMessage(ChatColor.RED + "You don't have permission:" +
                ChatColor.AQUA + this.getPermission() +
                ChatColor.RED + " to execute this command");
    }

    @Override
    public abstract boolean execute(CommandSender sender, String commandLabel, String[] args);

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException{
        return super.tabComplete(sender, alias, args);
    }

}
