package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.commands.ABFWithSubCommand;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class helpOption extends subOption {
    protected helpOption(ABFWithSubCommand parent) {
        super(parent, "help", "shows the help of all subcommands");
    }

    @Override
    public boolean execute(CommandSender sender, String label, ArrayList<String> args) {
        ChatBuilder builder = new ChatBuilder(sender, ChatColor.WHITE);
        builder.append(ChatColor.YELLOW, "====" + this.getParent().getName() + " Help====");
        for (subOption opt : this.getParent().getSubOptions().values()) {
            builder.newLine(ChatColor.WHITE).append(ChatColor.WHITE, "/abf ").append(ChatColor.AQUA, opt.getName())
                    .append(ChatColor.YELLOW, " = ").append(ChatColor.WHITE, opt.getHelpText());
        }
        builder.send();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }
}
