package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmSize;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class Defaults {
    public static void createDefaultFarms(AutoBuildFarms plugin) {
        ArrayList<FarmSize> sizes = new ArrayList<>();
        sizes.add(new FarmSize(plugin,
                "small", "§bSmall", "cactus_small",
                100, Collections.singletonList(new ItemStack(Material.CACTUS, 5)),
                Collections.singletonList(new numberItem(30, new ItemStack(Material.CACTUS, 1))),
                new ItemStack(Material.CACTUS, 1), new xyz(0, 0, 0)));
        sizes.add(new FarmSize(plugin,
                "medium", "§bMedium", "cactus_medium",
                100, Collections.singletonList(new ItemStack(Material.CACTUS, 20)),
                Collections.singletonList(new numberItem(10, new ItemStack(Material.CACTUS, 2))),
                new ItemStack(Material.CACTUS, 2), new xyz(0, 0, 0)));
        new FarmType(plugin,
                "cactus", "§2Cactus", sizes, new ItemStack(Material.CACTUS, 1));
    }
}
