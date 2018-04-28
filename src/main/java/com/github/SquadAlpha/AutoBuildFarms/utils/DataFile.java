package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.eventhandling.PlacedFarm;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import static com.github.SquadAlpha.AutoBuildFarms.utils.DataFile.cfgN.CHEST_LOCATIONS;
import static com.github.SquadAlpha.AutoBuildFarms.utils.DataFile.cfgN.FARM_SECTION;

public class DataFile {

    private static YamlConfiguration config;

    private static File getRealFile() {
        return new File(Reference.plugin.getDataFolder(), "data.yml");
    }

    public static void init() {
        config = YamlConfiguration.loadConfiguration(getRealFile());
        initFarms(getMainSection(FARM_SECTION));
    }

    public static void save() {
        Reference.placedFarms.forEach(PlacedFarm::save);
        try {
            config.save(getRealFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initFarms(ConfigurationSection mainSection) {
        Reference.placedFarms = new ArrayList<>();
        ConfigurationSection farmLocSect = getSubSection(CHEST_LOCATIONS, mainSection);
        for (String key : farmLocSect.getKeys(false)) {
            ConfigurationSection farmsect = farmLocSect.getConfigurationSection(key);
            Reference.placedFarms.add(new PlacedFarm(farmsect));
        }
    }

    public static ConfigurationSection getNewPlacedFarmSection() {
        ConfigurationSection farmsSect = getSubSection(CHEST_LOCATIONS, getMainSection(FARM_SECTION));
        UUID uuid = UUID.randomUUID();
        ConfigurationSection pFSect = getSubSection(uuid.toString(), farmsSect);
        pFSect.set("uuid", uuid);
        return pFSect;
    }

    public static void deletePF(PlacedFarm placedFarm) {
        ConfigurationSection farmsSect = getSubSection(CHEST_LOCATIONS, getMainSection(FARM_SECTION));
        farmsSect.set(placedFarm.getUuid().toString(), null);
    }

    private static ConfigurationSection getMainSection(cfgN sectionName) {
        return getMainSection(sectionName.toString());
    }

    private static ConfigurationSection getMainSection(String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(cfgN.PREFIX + "." + sectionName);
        if (section == null) {
            section = config.createSection(cfgN.PREFIX + "." + sectionName);
        }
        return section;
    }

    private static ConfigurationSection getSubSection(cfgN sectionName, ConfigurationSection headSection) {
        return getSubSection(sectionName.toString(), headSection);
    }

    private static ConfigurationSection getSubSection(String sectionName, ConfigurationSection headSection) {
        ConfigurationSection section = headSection.getConfigurationSection(sectionName);
        if (section == null) {
            section = headSection.createSection(sectionName);
        }
        return section;
    }

    public enum cfgN {
        CHEST_LOCATIONS("farm_locations"),
        FARM_SECTION("farms"),
        PREFIX(Reference.plugin.getDescription().getPrefix());

        private final String toString;


        cfgN(String name) {
            this.toString = name;
        }

        @Override
        public String toString() {
            return this.toString;
        }
    }

}
