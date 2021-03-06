package com.github.SquadAlpha.AutoBuildFarms.file;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import org.bukkit.configuration.ConfigurationSection;

public class Config {
    private final AutoBuildFarms plugin;
    private final ConfigurationSection data;

    public Config(AutoBuildFarms plugin) {
        ConfigurationSection data1;
        this.plugin = plugin;
        data1 = this.plugin.getConfig().getConfigurationSection(this.plugin.getDescription().getPrefix());
        if (data1 == null) {
            data1 = this.plugin.getConfig().createSection(this.plugin.getDescription().getPrefix());
        }
        this.data = data1;
        loadFarms();
    }


    private void loadFarms() {
        ConfigurationSection farms = this.data.getConfigurationSection("farms");
        if (farms == null) {
            farms = this.data.createSection("farms");
        }
        for (String key : farms.getKeys(false)) {
            new FarmType(this.plugin, farms.getConfigurationSection(key));
        }
    }


    public void save() {
        this.plugin.getRegistries().getFarmTypes().getObjects().forEach(FarmType::save);
        this.plugin.saveConfig();
    }

    public ConfigurationSection createSizeSection(String name) {
        ConfigurationSection sizeSection;
        ConfigurationSection head = this.data.getConfigurationSection("sizes");
        sizeSection = head.getConfigurationSection(name);
        if (sizeSection == null) {
            sizeSection = head.createSection(name);
        }
        return sizeSection;
    }

    public ConfigurationSection createFarmSection(String name) {
        ConfigurationSection head = this.data.getConfigurationSection("farms");
        ConfigurationSection farmSection;
        farmSection = head.getConfigurationSection(name);
        if (farmSection == null) {
            farmSection = head.createSection(name);
        }
        return farmSection;
    }
}
