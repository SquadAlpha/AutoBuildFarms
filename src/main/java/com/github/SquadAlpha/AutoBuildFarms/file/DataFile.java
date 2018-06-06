package com.github.SquadAlpha.AutoBuildFarms.file;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.PlacedFarm;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataFile {

    private final AutoBuildFarms plugin;
    private final YamlConfiguration config;
    private final ConfigurationSection data;

    public DataFile(AutoBuildFarms plugin) {
        ConfigurationSection data1;
        this.plugin = plugin;
        try {
            File f = new File(plugin.getDataFolder(), "data.yml");
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "data.yml"));
        data1 = this.config.getConfigurationSection(this.plugin.getDescription().getPrefix());
        if (data1 == null) {
            data1 = this.config.createSection(this.plugin.getDescription().getPrefix());
        }
        this.data = data1;
        loadFarms();
    }

    private void loadFarms() {
        ConfigurationSection sect = data.getConfigurationSection("placedfarms");
        if (sect == null) {
            sect = this.data.createSection("placedfarms");
        }
        for (String key : sect.getKeys(false)) {
            ((PlacedFarm) sect.get(key)).registerTasks();
        }
    }

    public ConfigurationSection getNewPlacedFarmConfigSection() {
        return data.createSection("farms." + UUID.randomUUID());
    }

    public void save() {
        if (this.data.getConfigurationSection("placedfarms") == null) {
            this.data.createSection("placedfarms");
        }
        ConfigurationSection pf = this.data.getConfigurationSection("placedfarms");
        this.plugin.getRegistries().getPlacedFarms().getObjects().forEach(o -> pf.set(o.getName(), o));
        try {
            File f = new File(this.plugin.getDataFolder(), "data.yml");
            f.getParentFile().mkdirs();
            f.createNewFile();
            this.config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFarm(PlacedFarm placedFarm) {
        ConfigurationSection pfSect = this.data.getConfigurationSection("placedfarms");
        if (pfSect == null) {
            pfSect = this.data.createSection("placedfarms");
        }
        this.plugin.getLog().info("Setting " + pfSect.getCurrentPath() + "." + placedFarm.getUuid() + " to null");
        pfSect.set(placedFarm.getUuid(), null);
    }
}
