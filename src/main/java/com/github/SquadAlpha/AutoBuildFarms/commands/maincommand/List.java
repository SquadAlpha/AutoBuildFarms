package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.commandArgs;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.function.Function;

public class List {

    @Getter
    private static final Function<commandArgs, Boolean> func = a -> {
        ChatBuilder cb = new ChatBuilder(a.getSender())
                .append(ChatColor.GOLD, "Farms:");
        ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().forEach(ft -> {

            cb.newLine().append(ChatColor.GOLD, " ")
                    .append(ChatColor.RESET, ft.getFancyName())
                    .append(ChatColor.GOLD, ":")
                    .append(ChatColor.WHITE, ft.getName());
            ft.getSizes().forEach(fs -> cb.newLine().append(ChatColor.GOLD, "  ")
                    .append(ChatColor.RESET, fs.getFancyName())
                    .append(ChatColor.GOLD, ":")
                    .append(ChatColor.AQUA, ((AutoBuildFarms) a.getCmd().getPlugin()).getEcon().format(fs.getPrice()))
                    .append(ChatColor.WHITE, " " + fs.getName()));
        });
        cb.send();
        return true;
    };

    @Getter
    private static final Function<commandArgs, java.util.List<String>> tabComplete = a -> new ArrayList<>();
}
