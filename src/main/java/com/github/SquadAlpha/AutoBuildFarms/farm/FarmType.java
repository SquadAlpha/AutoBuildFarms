package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.Registry;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FarmType implements RegistryObject {//TODO convert to ConfigurationSerializable

    private final AutoBuildFarms plugin;
    private final String name;
    private final String fancyName;
    private final Registry<FarmSize> sizes;
    private final ItemStack displayItem;

    private final ConfigurationSection sect;

    public FarmType(AutoBuildFarms plugin, ConfigurationSection sect) {
        this.plugin = plugin;
        this.name = sect.getString("name", "NONAMEGIVEN");
        this.fancyName = sect.getString("fancyName", "§6NONAMEGIVEN");
        this.displayItem = sect.getItemStack("item", new ItemStack(Material.STICK));
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.setDisplayName(this.fancyName);
        this.displayItem.setItemMeta(meta);
        this.sizes = new Registry<>();
        ((List<FarmSize>) sect.getList("sizes", new ArrayList<FarmSize>())).forEach(this.sizes::add);
        this.sect = sect;
        this.plugin.getRegistries().getFarmTypes().add(this);
        this.redoSizes();
    }

    public void redoSizes() {
        this.sizes.forEach(s -> s.setParent(this));
    }

    public FarmType(AutoBuildFarms plugin, String name, String fancyName, List<FarmSize> sizes, ItemStack displayItem) {
        this.plugin = plugin;
        this.name = name;
        this.fancyName = fancyName;
        this.sizes = new Registry<>();
        sizes.forEach(this.sizes::add);
        this.displayItem = displayItem;
        this.sect = this.getPlugin().getConfigFile().createFarmSection(this.getName());
        this.plugin.getRegistries().getFarmTypes().add(this);
        this.redoSizes();
    }

    public ConfigurationSection save() {
        this.sect.set("name", this.name);
        this.sect.set("fancyName", this.fancyName);
        this.sect.set("sizes", this.sizes.getObjects());
        this.sect.set("item", this.displayItem);
        return this.sect;
    }

    public String toString() {
        return "FarmType(name=" + this.getName() + ", fancyName=" + this.getFancyName() + ", sizes=" + this.getSizes() + ", displayItem=" + this.getDisplayItem() + ")";
    }
}
