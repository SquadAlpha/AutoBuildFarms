package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmSize;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

import static org.bukkit.Bukkit.getServer;

public class Defaults {
    public static void createDefaultFarms(AutoBuildFarms plugin) {
        ArrayList<FarmSize> sizes = new ArrayList<>();
        sizes.add(new FarmSize(plugin,
                "Cactus_small", "§6Small", "cactus_small",
                100, Collections.singletonList(new ItemStack(Material.CACTUS, 5)),
                Collections.singletonList(new numberItem(20, new ItemStack(Material.CACTUS, 1))),
                new ItemStack(Material.CACTUS, 1), new Location(getServer().getWorlds().get(0), 0, 0, 0)));
        sizes.add(new FarmSize(plugin,
                "Cactus_medium", "§6Medium", "cactus_medium",
                100, Collections.singletonList(new ItemStack(Material.CACTUS, 20)),
                Collections.singletonList(new numberItem(5, new ItemStack(Material.CACTUS, 2))),
                new ItemStack(Material.CACTUS, 2), new Location(getServer().getWorlds().get(0), 0, 0, 0)));
        new FarmType(plugin,
                "Cactus", "§6Cactus", sizes, new ItemStack(Material.CACTUS, 1));
    }
}
