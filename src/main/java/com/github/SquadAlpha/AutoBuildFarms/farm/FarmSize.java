package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.file.Schematic;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.numberItem;
import lombok.Getter;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FarmSize extends RegistryObject {
    private final AutoBuildFarms plugin;
    private final String name;
    private final String fancyName;
    private final String schemName;
    private final Schematic schem;
    private final int price;
    private final List<ItemStack> materials;
    private final List<numberItem> revenue; // And itemstack every long ticks
    private final ItemStack displayItem;
    private final Location chestLoc;

    private final ConfigurationSection configSect;


    public FarmSize(AutoBuildFarms plugin, ConfigurationSection sect) {
        this.plugin = plugin;
        this.name = sect.getString("name", "NONAMEGIVEN");
        this.fancyName = sect.getString("fancyname", "§6NONAMEGIVEN");
        this.schemName = sect.getString("schemfile", this.name + ".schematic");
        this.schem = new Schematic(this.schemName);
        this.price = sect.getInt("price", 100);
        this.displayItem = sect.getItemStack("item", new ItemStack(Material.STICK));
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.setDisplayName(this.fancyName);
        this.displayItem.setItemMeta(meta);
        this.chestLoc = new Location(null,
                sect.getInt("chestlocation.x", 0),
                sect.getInt("chestlocation.y", 0),
                sect.getInt("chestlocation.z", 0));

        List<?> mats = sect.getList("materials");
        if (mats != null) {
            if (mats.get(0) instanceof ItemStack) {
                this.materials = (List<ItemStack>) mats;
            } else {
                this.materials = new ArrayList<>();
                this.materials.add(new ItemStack(Material.STICK));
            }
        } else {
            this.materials = new ArrayList<>();
            this.materials.add(new ItemStack(Material.STICK));
        }


        List<?> reve = sect.getList("revenue");
        if (reve != null && reve.get(0) instanceof numberItem) {
            this.revenue = (List<numberItem>) reve;
        } else {
            this.revenue = new ArrayList<>();
            this.revenue.add(new numberItem(10, ItemStackBuilder.of(Material.STICK).name("NO Revenue Set in convig").build()));
        }

        this.configSect = sect;
        this.plugin.getRegistries().getFarmSizes().add(this);
    }

    public FarmSize(AutoBuildFarms plugin, String name, String fancyName, String schemName, int price, List<ItemStack> materials, List<numberItem> revenue, ItemStack displayItem, Location chestLoc) {
        this.plugin = plugin;
        this.name = name;
        this.fancyName = fancyName;
        this.schemName = schemName;
        this.schem = new Schematic(this.schemName);
        this.price = price;
        this.materials = materials;
        this.revenue = revenue;
        this.displayItem = displayItem;
        this.chestLoc = chestLoc;
        this.configSect = this.getPlugin().getConfigFile().createSizeSection(this.getName());
        this.plugin.getRegistries().getFarmSizes().add(this);
    }

    public ConfigurationSection save() {
        this.configSect.set("name", this.name);
        this.configSect.set("fancyname", this.fancyName);
        this.configSect.set("schemfile", this.schemName);
        this.configSect.set("price", this.price);
        this.configSect.set("materials", this.materials);
        this.configSect.set("revenue", this.revenue);
        this.configSect.set("item", this.displayItem);
        this.configSect.set("chestlocation.x", this.chestLoc.getBlockX());
        this.configSect.set("chestlocation.y", this.chestLoc.getBlockY());
        this.configSect.set("chestlocation.z", this.chestLoc.getBlockZ());
        return this.configSect;
    }

}
