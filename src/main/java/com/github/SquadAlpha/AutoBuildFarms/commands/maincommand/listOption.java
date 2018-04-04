package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class listOption extends subOption {
    public listOption(ABFMain abfMain) {
        super(abfMain, "list", "list the available farms");
    }

    @Override
    public boolean execute(CommandSender sender, String label, ArrayList<String> args) {
        ChatBuilder buider = new ChatBuilder(sender, ChatColor.WHITE);
        buider.append(ChatColor.YELLOW, "====Farms====");

        Reference.farmList.forEach((fname, farm) -> buider.newLine(ChatColor.WHITE)
                .append(ChatColor.YELLOW, farm.getFancyName())
                .append(ChatColor.YELLOW, " = ")
                .append(ChatColor.WHITE, farm.getName())
                .append(ChatColor.AQUA, "->")
                .append(ChatColor.GOLD, Arrays.toString(farm.getSizes().keySet().toArray())));
        buider.send();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        return null;
    }
}
