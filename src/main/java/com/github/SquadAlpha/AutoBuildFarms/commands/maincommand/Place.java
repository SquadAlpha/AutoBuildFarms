package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmSize;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.farm.PlacedFarm;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.commandArgs;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Place {

    @Getter
    private static final Function<commandArgs, Boolean> func = a -> {


        if (a.getArgs().length > 1 && ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().searchFirst(a.getArgs()[1]) != null) {
            String sFarm = a.getArgs()[1];
            FarmType farm = ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().searchFirst(sFarm);
            if (a.getArgs().length > 2) {
                String sSize = a.getArgs()[2];
                FarmSize size = farm.getSizes().searchFirst(sSize);
                if (size != null) {
                    if (size.canBePlacedByAndYell(a.getSender())) {
                        PlacedFarm pf = new PlacedFarm(((AutoBuildFarms) a.getCmd().getPlugin()), size, ((Player) a.getSender()).getLocation());
                        ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getPlacedFarms().add(pf);
                        size.getSchematic().place(((Player) a.getSender()).getLocation());
                        pf.registerTasks();
                        new ChatBuilder(a.getSender())
                                .append(ChatColor.GREEN, "Placed: ")
                                .append(ChatColor.GOLD, pf.getSize().getParent().getFancyName())
                                .append(ChatColor.GOLD, " ")
                                .append(ChatColor.GOLD, pf.getSize().getFancyName())
                                .append(ChatColor.GREEN, " successfully")
                                .send();

                    }
                } else {
                    ABFMain.invalidSomethingSpiel("size", farm.getSizes().getObjects(), a.getSender());
                }
            } else {
                ABFMain.invalidSomethingSpiel("size", farm.getSizes().getObjects(), a.getSender());
            }
        } else {
            ABFMain.invalidSomethingSpiel("farm", ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().getObjects(), a.getSender());
        }
        return true;
    };

    @Getter
    private static final Function<commandArgs, List<String>> tabComplete = a -> {
        ArrayList<String> suggestions = new ArrayList<>();
        if (a.getArgs().length <= 1) {
            ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().forEach(ft -> suggestions.add(ft.getName()));
        } else if (a.getArgs().length <= 2) {
            String search = a.getArgs()[1];
            ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().forEach(ft -> {
                if (a.getSender().hasPermission(ft.getPlacePermission())) {
                    if (ft.getName().startsWith(search)) {
                        suggestions.add(ft.getName());
                    }
                }
            });
        } else if (a.getArgs().length <= 3) {
            String farmName = a.getArgs()[1];
            FarmType farm = ((AutoBuildFarms) a.getCmd().getPlugin()).getRegistries().getFarmTypes().searchFirst(farmName);
            if (farm != null) {
                String search = a.getArgs()[2];
                farm.getSizes().forEach(fs -> {
                    if (a.getSender().hasPermission(fs.getPlacePermission())) {
                        if (fs.getName().startsWith(search)) {
                            suggestions.add(fs.getName());
                        }
                    }
                });
            }
        }


        return suggestions;
    };

}
