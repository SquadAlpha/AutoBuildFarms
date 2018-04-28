package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.config.Config;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;

import static com.github.SquadAlpha.AutoBuildFarms.reference.Reference.log;

public class DefaultFarm {
    public static void add(AutoBuildFarms plugin) {
        log.info("No farms found in config adding default one");
        Farm f = new Farm("cactus", "&6&lCactus", new ItemStack(Material.CACTUS, 1));
        HashMap<ItemStack, Long> rev = new HashMap<>();
        rev.put(new ItemStack(Material.CACTUS, 1), 80L);
        f.addSize("small", "&bSmall", "CactusSmall.schematic", 100, new ItemStack(Material.CACTUS, 1), Collections.singletonList(new ItemStack(Material.CACTUS, 1)), rev);
        rev.put(new ItemStack(Material.CACTUS, 2), 20L);
        f.addSize("medium", "&bMedium", "CactusMedium.schematic", 500, new ItemStack(Material.CACTUS, 2), Collections.singletonList(new ItemStack(Material.CACTUS, 25)), rev);
        Config.addFarm(f);
        Config.save();
        Config.reload();
        Config.init();
    }
}
