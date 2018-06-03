package com.github.SquadAlpha.AutoBuildFarms.farm;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

@Getter
public class PlacedFarm extends RegistryObject {

    private final FarmSize size;
    private final Location placeLoc;
    private final Chest chest;
    private final boolean built;

    private final ConfigurationSection configSect;
    private final AutoBuildFarms plugin;
    private final ArrayList<Task> tmp;

    public PlacedFarm(AutoBuildFarms plugin, FarmSize size, Location placeLoc) {
        this.plugin = plugin;
        this.size = size;
        this.placeLoc = placeLoc;
        Location chestLock = this.size.getChestLoc();
        chestLock.setWorld(this.placeLoc.getWorld());
        Block b = this.placeLoc.add(chestLock).getBlock();
        if (!b.getType().equals(Material.CHEST)) {
            b.setType(Material.CHEST);
        }
        this.chest = (Chest) b;
        this.built = true;//TODO change to false when building works
        this.configSect = this.plugin.getDataFile().getPlacedFarmConfigSection();
        this.plugin.getRegistries().getPlacedFarms().add(this);
        this.tmp = new ArrayList<>();
        registerTasks();
    }

    public PlacedFarm(AutoBuildFarms plugin, ConfigurationSection sect) {
        this.plugin = plugin;
        this.size = this.plugin.getRegistries().getFarmSizes().lookupName(sect.getString("size", "NOSIZEPROVIDED"));
        this.placeLoc = (Location) sect.get("placelocation", new Location(getServer().getWorlds().get(0), 0, 0, 0));
        Location cLock = (Location) sect.get("chestlocation", new Location(getServer().getWorlds().get(0), 0, 0, 0));
        if (!cLock.getBlock().getType().equals(Material.CHEST)) cLock.getBlock().setType(Material.CHEST);
        this.chest = (Chest) cLock.getBlock();
        this.built = sect.getBoolean("built", true);
        this.configSect = sect;
        this.tmp = new ArrayList<>();
        registerTasks();
    }

    private void registerTasks() {
        this.unRegisterTasks();
        if (this.built) {
            Scheduler s = Schedulers.async();
            this.size.getRevenue().forEach(r -> this.tmp.add(s.runRepeating(() -> {
                if (this.getChest() != null && this.getChest().getBlockInventory() != null) {
                    this.getChest().getBlockInventory().addItem(r.getItem());
                } else {
                    this.delete();
                }
            }, 1, r.getNum())));
        } else {
            //TODO register building handlers
        }
    }

    public void save() {
        unRegisterTasks();
        this.configSect.set("size", this.size.getName());
        this.configSect.set("placelocation", this.placeLoc);
        this.configSect.set("chestlocation", this.chest.getLocation());
        this.configSect.set("built", this.built);
    }

    public void delete() {
        this.unRegisterTasks();
        this.plugin.getDataFile().deleteFarm(this);
        this.plugin.getRegistries().getPlacedFarms().remove(this);
    }

    public void unRegisterTasks() {
        this.tmp.forEach(t -> {
            t.close();
            this.tmp.remove(t);
        });
    }

    @Override
    public String getName() {
        return this.configSect.getName();
    }
}
