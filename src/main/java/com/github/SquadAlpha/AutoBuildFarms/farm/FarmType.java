package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FarmType extends RegistryObject {

    private final AutoBuildFarms plugin;
    private final String name;
    private final String fancyName;
    private final List<FarmSize> sizes;
    private final ItemStack displayItem;

    private final ConfigurationSection sect;

    public FarmType(AutoBuildFarms plugin, ConfigurationSection sect) {
        this.plugin = plugin;
        this.name = sect.getString("name", "NONAMEGIVEN");
        this.fancyName = sect.getString("fancyname", "§6NONAMEGIVEN");
        this.displayItem = sect.getItemStack("item", new ItemStack(Material.STICK));
        ItemMeta meta = this.displayItem.getItemMeta();
        meta.setDisplayName(this.fancyName);
        this.displayItem.setItemMeta(meta);
        List<String> sizes1;
        sizes1 = sect.getStringList("sizes");
        if (sizes1 == null) {
            sizes1 = new ArrayList<>();
        }
        this.sizes = new ArrayList<>();
        sizes1.forEach(s -> this.sizes.add(this.plugin.getRegistries().getFarmSizes().lookupName(s)));
        this.sect = sect;
        this.plugin.getRegistries().getFarmTypes().add(this);
    }

    public FarmType(AutoBuildFarms plugin, String name, String fancyname, List<FarmSize> sizes, ItemStack displayItem) {
        this.plugin = plugin;
        this.name = name;
        this.fancyName = fancyname;
        this.sizes = sizes;
        this.displayItem = displayItem;
        this.sect = this.getPlugin().getConfigFile().createFarmSection(this.getName());
        this.plugin.getRegistries().getFarmTypes().add(this);
    }

    public ConfigurationSection save() {
        this.sect.set("name", this.name);
        this.sect.set("fancyname", this.fancyName);
        ArrayList<String> nsizes = new ArrayList<>();
        this.sizes.forEach(s -> nsizes.add(s.getName()));
        this.sect.set("sizes", nsizes);
        this.sect.set("item", this.displayItem);
        return this.sect;
    }
}
