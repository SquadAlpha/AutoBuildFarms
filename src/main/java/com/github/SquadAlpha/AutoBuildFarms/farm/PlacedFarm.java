package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlacedFarm implements RegistryObject, ConfigurationSerializable {

    private final String uuid;
    private final FarmSize size;
    private final Location placeLoc;
    private final Location chestLocation;
    private final boolean built;

    private final AutoBuildFarms plugin;
    private final ArrayList<Task> tmp;
    private boolean running;

    public PlacedFarm(Map<String, Object> map) {
        this.uuid = (String) map.get("uuid");
        this.size = (FarmSize) map.get("size");
        this.placeLoc = (Location) map.get("loc");
        if (!((Location) map.get("chestloc")).getBlock().getType().equals(Material.CHEST)) {
            ((Location) map.get("chestloc")).getBlock().setType(Material.CHEST);
        }
        this.chestLocation = (Location) map.get("chestloc");
        this.built = (boolean) map.get("built");
        this.tmp = new ArrayList<>();
        this.plugin = AutoBuildFarms.getPlugin();
        this.plugin.getRegistries().getPlacedFarms().add(this);
    }

    public PlacedFarm(AutoBuildFarms plugin, FarmSize size, Location placeLoc) {
        this.plugin = plugin;
        this.uuid = UUID.randomUUID().toString();
        this.size = size;
        this.placeLoc = placeLoc;
        xyz tloc = this.size.getChestOffset();
        Location chestloc = this.placeLoc.clone().add(tloc.getX(), tloc.getY(), tloc.getZ());
        Block b = chestloc.getBlock();
        if (!b.getType().equals(Material.CHEST)) {
            b.setType(Material.CHEST);
        }
        this.chestLocation = this.placeLoc.clone().add(tloc.getVector());
        this.built = true;//TODO change to false when building works
        this.plugin.getRegistries().getPlacedFarms().add(this);
        this.tmp = new ArrayList<>();
    }

    public void registerTasks() {
        this.unRegisterTasks();
        if (this.built) {
            Scheduler s = Schedulers.async();
            this.running = true;
            this.size.getRevenue().forEach(r -> this.tmp.add(s.runRepeating(() -> {
                try {
                    if (!this.isRunning()) {
                        return;
                    }
                    if (this.getChestLocation() != null &&
                            this.getChestLocation().getBlock() != null &&
                            this.getChestLocation().getBlock().getState() != null &&
                            this.getChestLocation().getBlock().getState() instanceof Chest) {
                        ((Chest) this.getChestLocation().getBlock().getState()).getBlockInventory().addItem(r.getItem());
                    } else {
                        this.delete();
                    }
                } catch (IllegalStateException e) {
                    this.delete();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }, 1, r.getNum())));
        } else {
            //TODO register building handlers
        }
    }


    public void delete() {
        this.running = false;
        if (!this.plugin.getRegistries().getPlacedFarms().remove(this)) {
            this.plugin.getRegistries().getPlacedFarms().remove(this.plugin.getRegistries().getPlacedFarms().get(this.getName()));
        }
        this.unRegisterTasks();
        this.plugin.getDataFile().deleteFarm(this);
    }

    public synchronized void unRegisterTasks() {
        this.tmp.forEach(t -> {
            t.close();
            this.tmp.remove(t);
        });
    }

    @Override
    public String getName() {
        return this.getUuid();
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uuid", this.getUuid());
        map.put("size", this.getSize());
        map.put("loc", this.getPlaceLoc().getBlock().getLocation()); // this converts the doubles in location to ints
        map.put("chestloc", this.getChestLocation().getBlock().getLocation());
        map.put("built", this.isBuilt());
        return map;
    }
}
