package com.github.SquadAlpha.AutoBuildFarms.commands;


import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class ABFCommand extends Command implements TabCompleter, CommandExecutor {


    protected ABFCommand(String name, String description, String usageMessage, String... aliases){
        super(name, description, usageMessage, Arrays.asList(aliases));
        this.setPermission(Reference.plugin.getName().toLowerCase() + "." + name);
        this.setPermissionMessage(ChatColor.RED + "You don't have permission:" +
                ChatColor.AQUA + this.getPermission() +
                ChatColor.RED + " to execute this command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (Reference.perms.has(sender, this.getPermission())) {
            return this._execute(sender, commandLabel, args);
        } else {

            new ChatBuilder(sender, org.bukkit.ChatColor.RED)
                    .append(org.bukkit.ChatColor.RED, "You don't have permission:")
                    .append(org.bukkit.ChatColor.AQUA, this.getPermission())
                    .append(org.bukkit.ChatColor.RED, " to exectute this command")
                    .send();
            return false;
        }
    }


    public abstract boolean _execute(CommandSender sender, String commandLabel, String[] args);
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.getName().equalsIgnoreCase(command.getName()) && this.execute(sender, label, args);
    }


    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected boolean isPlayerAndYell(CommandSender sender) {
        if (this.isPlayer(sender)) {
            return true;
        } else {
            new ChatBuilder(sender, org.bukkit.ChatColor.RED)
                    .append(org.bukkit.ChatColor.RED, "This command can only be exectuted by a Player.")
                    .newLine(org.bukkit.ChatColor.YELLOW)
                    .append(org.bukkit.ChatColor.YELLOW, "You are:" + sender.getName() + ":" + sender.toString())
                    .send();
            return false;
        }
    }

}
