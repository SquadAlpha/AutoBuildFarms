package com.github.SquadAlpha.AutoBuildFarms.eventhandling;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.DataFile;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

//TODO check if schematic is still complete
//TODO make the schematic build when provided with resources

public class PlacedFarm {
    @Getter
    private final UUID uuid;
    private final Farm.Size farm;
    private final Chest chest;
    private final ConfigurationSection configsect;
    private boolean built;
    private ArrayList<Task> tasks;


    public PlacedFarm(Farm.Size s, Location loca) {
        this.farm = s;
        if (!(loca.getBlock().getType() == Material.CHEST)) {
            loca.getBlock().setType(Material.CHEST);
        }
        this.chest = (Chest) loca.getBlock().getState();
        this.configsect = DataFile.getNewPlacedFarmSection();
        this.uuid = UUID.fromString(this.configsect.getString("uuid"));
        this.built = true; //TODO change to false when contruction is working
        registerTasks();
    }

    public PlacedFarm(ConfigurationSection dataSect) {
        this.uuid = UUID.fromString(dataSect.getString("uuid", UUID.randomUUID().toString()));
        this.configsect = dataSect;
        Farm rfarm = Reference.farmList.get(this.configsect.getString("farm"));
        this.farm = rfarm.getSizes().get(this.configsect.getString("size"));
        Location loca = new Location(
                Reference.plugin.getServer().getWorld(this.configsect.getString("world")),
                this.configsect.getInt("x"),
                this.configsect.getInt("y"),
                this.configsect.getInt("z"));
        if (!(loca.getBlock().getType() == Material.CHEST)) {
            loca.getBlock().setType(Material.CHEST);
        }
        this.chest = (Chest) loca.getBlock().getState();
        this.built = this.configsect.getBoolean("built", true);
        registerTasks();
    }

    private void registerTasks() {
        if (tasks == null) {
            this.tasks = new ArrayList<>();
        }
        if (this.built) {
            for (Map.Entry<ItemStack, Long> e : this.farm.getRevenue().entrySet()) {
                this.tasks.add(
                        Schedulers.sync().runRepeating(new itemTask(this.chest, e.getKey(), this),
                                10,
                                e.getValue())
                );
            }
        }
    }

    private void destroy() {
        this.tasks.forEach(Task::close);
        this.configsect.getKeys(false).forEach(s -> this.configsect.set(s, null));
        DataFile.deletePF(this);
        Reference.placedFarms.remove(this);
    }

    public void save() {
        this.tasks.forEach(Task::close);
        this.configsect.set("farm", farm.getParent().getName());
        this.configsect.set("size", farm.getName());
        Location loc = this.chest.getLocation();
        this.configsect.set("world", loc.getWorld().getName());
        this.configsect.set("x", loc.getBlockX());
        this.configsect.set("y", loc.getBlockY());
        this.configsect.set("z", loc.getBlockZ());
        this.configsect.set("built", this.built);
        this.configsect.set("uuid", this.uuid.toString());
    }

    private static class itemTask implements Runnable {


        private final Chest chest;
        private final ItemStack item;
        private final PlacedFarm parent;

        private itemTask(Chest chest, ItemStack item, PlacedFarm parent) {
            this.chest = chest;
            this.item = item;
            this.parent = parent;
        }

        @Override
        public void run() {
            if (this.chest.getBlock().getType() == Material.CHEST) {
                this.chest.getBlockInventory().addItem(this.item);
                Reference.log.info("Placed " + this.item.toString() + " into " + this.chest.getLocation());
            } else {
                this.parent.destroy();
            }
        }
    }


}
