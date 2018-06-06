package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.file.Schematic;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.numberItem;
import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class FarmSize implements RegistryObject, ConfigurationSerializable {
    private final AutoBuildFarms plugin;
    private final String name;
    private final String fancyName;
    private final String schemName;
    private final Schematic schem;
    private final int price;
    private final List<ItemStack> materials;
    private final List<numberItem> revenue;
    private final ItemStack displayItem;
    private final xyz chestOffset;

    private FarmType parent;

    public FarmSize(AutoBuildFarms plugin, String name, String fancyName, String schemName, int price,
                    List<ItemStack> materials, List<numberItem> revenue, ItemStack displayItem, xyz chestOffset) {
        this.plugin = plugin;
        this.name = name;
        this.fancyName = fancyName;
        this.schemName = schemName;
        this.schem = new Schematic(this.schemName);
        this.price = price;
        this.materials = materials;
        this.revenue = revenue;
        this.displayItem = displayItem;
        this.chestOffset = chestOffset;
    }

    public FarmSize(Map<String, Object> map) {
        this((AutoBuildFarms) Bukkit.getPluginManager().getPlugin("AutoBuildFarms"),
                (String) map.get("name"), (String) map.get("fancyname"), (String) map.get("schemfile"),
                (int) map.get("price"), (List<ItemStack>) map.get("materials"), (List<numberItem>) map.get("revenue"),
                (ItemStack) map.get("item"), (xyz) map.get("chestoffset"));
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chestoffset", this.chestOffset);
        map.put("fancyname", this.fancyName);
        map.put("item", this.displayItem);
        map.put("materials", this.materials);
        map.put("name", this.name);
        map.put("price", this.price);
        map.put("revenue", this.revenue);
        map.put("schemfile", this.schemName);
        return map;
    }

    public boolean canBePlacedBy(CommandSender sender) {
        return sender.hasPermission(this.plugin.getDescription().getPrefix() + ".place." +
                this.parent.getName() + "." + this.getName());
        //TODO check money and building materials
    }

    public void setParent(FarmType farmType) {
        this.parent = farmType;
    }
}
