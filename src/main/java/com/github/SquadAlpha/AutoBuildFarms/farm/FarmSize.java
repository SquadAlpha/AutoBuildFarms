package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.file.Schematic;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.numberItem;
import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class FarmSize implements RegistryObject, ConfigurationSerializable {
    private final AutoBuildFarms plugin;
    private final String name;
    private final String fancyName;
    private final String schematicName;
    private final Schematic schematic;
    private final double price;
    private final List<ItemStack> materials;
    private final List<numberItem> revenue;
    private final ItemStack displayItem;
    private final xyz chestOffset;

    private FarmType parent;

    public FarmSize(AutoBuildFarms plugin, String name, String fancyName, String schematicName, double price,
                    List<ItemStack> materials, List<numberItem> revenue, ItemStack displayItem, xyz chestOffset) {
        this.plugin = plugin;
        this.name = name;
        this.fancyName = fancyName;
        this.schematicName = schematicName;
        Schematic schematic1;
        try {
            schematic1 = Schematic.load(this.getPlugin(), this.schematicName, this.schematicName);
        } catch (IOException e) {
            this.plugin.getLog().severe(e.getLocalizedMessage());
            this.plugin.getLog().severe(e.getMessage());
            this.plugin.getLog().throwing(this.getClass().getCanonicalName(), "init", e);
            schematic1 = Schematic.chest(this.getPlugin(), this.getSchematicName());
        }
        this.schematic = schematic1;
        this.price = price;
        this.materials = materials;
        this.revenue = revenue;
        this.displayItem = displayItem;
        this.chestOffset = chestOffset;
    }

    public FarmSize(Map<String, Object> map) {
        this((AutoBuildFarms) Bukkit.getPluginManager().getPlugin("AutoBuildFarms"),
                (String) map.get("name"), (String) map.get("fancyName"), (String) map.get("schematicFile"),
                (double) map.get("price"), (List<ItemStack>) map.get("materials"), (List<numberItem>) map.get("revenue"),
                (ItemStack) map.get("item"), (xyz) map.get("chestOffset"));
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("chestOffset", this.chestOffset);
        map.put("fancyName", this.fancyName);
        map.put("item", this.displayItem);
        map.put("materials", this.materials);
        map.put("name", this.name);
        map.put("price", this.price);
        map.put("revenue", this.revenue);
        map.put("schematicFile", this.schematicName);
        return map;
    }


    public String getPlacePermission() {
        return this.plugin.getDescription().getPrefix() + ".place." +
                this.parent.getName() + "." + this.getName();
    }

    public boolean canBePlacedByAndYell(CommandSender sender) {
        ChatBuilder builder = new ChatBuilder(sender);
        boolean success = false;
        if (sender.hasPermission(this.getPlacePermission())) {
            //TODO check if space is free
            //TODO check if enough money money
            //Both of these tasks before this material checking step
            if(sender instanceof Player){
                //// Mat check starts here
                Player player = ((Player) sender);
                boolean hasitems = true;
                ArrayList<ItemStack> removed = new ArrayList<>();
                ItemStack missing = null;
                for(ItemStack mat:this.getMaterials()){
                    if(player.getInventory().contains(mat)){
                        player.getInventory().remove(mat);
                        removed.add(mat);
                    }else{
                        hasitems = false;
                        missing = mat;
                    }
                }
                if(hasitems){
                    success = true;
                }else{
                    player.getInventory().addItem(removed.toArray(new ItemStack[0]));
                    builder.append(ChatColor.RED,"You don't have enough items")
                            .append(ChatColor.YELLOW,"You are missing at least:")
                            .append(ChatColor.LIGHT_PURPLE,missing.getType().toString())
                            .append(ChatColor.DARK_PURPLE,"x")
                            .append(ChatColor.LIGHT_PURPLE, String.valueOf(missing.getAmount()));
                }
                //// Mat check ends here
            }else{
                builder.append(ChatColor.RED,"This command can only be executed by a player").newLine()
                        .append(ChatColor.YELLOW,"You are:")
                        .append(ChatColor.GREEN,sender.getName())
                        .append(ChatColor.WHITE,":")
                        .append(ChatColor.GOLD,sender.toString());
            }

        } else {
            builder.append(ChatColor.RED, "You don't have permission:")
                    .append(ChatColor.AQUA, this.getPlacePermission())
                    .append(ChatColor.RED, " to place ")
                    .append(ChatColor.GOLD, this.parent.getFancyName())
                    .append(ChatColor.WHITE, " ")
                    .append(ChatColor.GOLD, this.getFancyName());
        }
        builder.send();
        return success;
    }

    public void setParent(FarmType farmType) {
        this.parent = farmType;
    }
}
