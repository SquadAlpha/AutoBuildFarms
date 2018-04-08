package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class Farm{

    @Getter
    private String name;
    @Getter
    private String fancyName;
    @Getter
    private HashMap<String, Size> sizes;
    @Getter
    private ItemStack displayItem;

    public Farm(String name, String fancyName, ItemStack displayItem) {
        this.name = name.toLowerCase();
        this.fancyName = fancyName;
        this.sizes = new HashMap<>();
        this.displayItem = displayItem;
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.setDisplayName(this.fancyName);
        this.displayItem.setItemMeta(meta);
    }

    public boolean hasSize(String sizeName){
        return sizes.containsKey(sizeName);
    }

    public void addSize(String sname, String fancyName, String schematic, int price, ItemStack displayItem, List<ItemStack> materials) {
        ItemMeta meta = displayItem.getItemMeta();
        meta.setDisplayName(fancyName);
        displayItem.setItemMeta(meta);
        sizes.put(sname.toLowerCase(),
                new Size(sname.toLowerCase(), fancyName, schematic, Building.loadSchematic(schematic), price, materials, displayItem));
    }

    @Data
    public class Size{
        private final String name;
        private final String fancyName;
        private final String schemName;
        private final Building schem;
        private final int price;
        private final List<ItemStack> materials;
        private final ItemStack displayItem;
    }
}
