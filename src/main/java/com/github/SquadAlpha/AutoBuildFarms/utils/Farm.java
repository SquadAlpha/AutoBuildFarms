package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class Farm{

    @Getter
    private String name;
    @Getter
    private String fancyName;
    @Getter
    private HashMap<String, Size> sizes;

    public Farm(String name, String fancyName){
        this.name = name;
        this.fancyName = fancyName;
        this.sizes = new HashMap<>();
    }

    public boolean hasSize(String sizeName){
        return sizes.containsKey(sizeName);
    }

    public void addSize(String sname, String fancyName, String schematic, int price, List<ItemStack> resources){
        sizes.put(sname,
                new Size(sname, fancyName, schematic, Building.loadSchematic(schematic), price, resources));
    }

    @Data
    public class Size{
        private final String name;
        private final String fancyName;
        private final String schemName;
        private final Building schem;
        private final int price;
        private final List<ItemStack> resources;
    }
}
