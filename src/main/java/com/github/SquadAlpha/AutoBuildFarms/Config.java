package com.github.SquadAlpha.AutoBuildFarms;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static com.github.SquadAlpha.AutoBuildFarms.Reference.farmList;
import static com.github.SquadAlpha.AutoBuildFarms.Reference.plugin;

public class Config{


    private static FileConfiguration getConfig(){
        return plugin.getConfig();
    }

    public static void init(){
        plugin.getConfig().options().copyHeader(true);
        plugin.getConfig().options().copyDefaults(true);
        initFarms(getMainSection(cfgN.FARMS_SECTION));
        initGeneral(getMainSection(cfgN.GENERAL_SECTION));
    }

    private static void initGeneral(ConfigurationSection configurationSection){
        Reference.loreHeader = configurationSection.getString(cfgN.ITEM_LORE_HEADER.toString(), "&6&k|&r&6AutoFarm&k|&r");
        Reference.farmBlock = configurationSection.getItemStack(cfgN.FARMBLOCK.toString(), new ItemStack(Material.CHEST));
    }

    public static void reload(){
        plugin.reloadConfig();
        init();
    }

    public static void save(){
        //FARMS
        ConfigurationSection farms = getMainSection(cfgN.FARMS_SECTION);
        for(Farm farm : farmList.values()){
            ConfigurationSection fSect = getSubSection(farm.getName(), farms);
            ConfigurationSection sSect = getSubSection(cfgN.SIZES_SECTION, fSect);

            fSect.set(cfgN.FORMATTED_NAME.toString(), farm.getFancyName());

            for(Farm.Size s : farm.getSizes().values()){
                ConfigurationSection sizeSect = getSubSection(s.getName(), sSect);

                sizeSect.set(cfgN.FORMATTED_NAME.toString(), s.getFancyName());
                sizeSect.set(cfgN.SCHEMATIC_FILE.toString(), s.getSchemName());
                sizeSect.set(cfgN.PRICE.toString(), s.getPrice());
                saveResources(s.getResources(), sizeSect);
            }
        }

        //GENERAL
        ConfigurationSection general = getMainSection(cfgN.GENERAL_SECTION);
        general.set(cfgN.ITEM_LORE_HEADER.toString(), Reference.loreHeader);
        general.set(cfgN.FARMBLOCK.toString(), Reference.farmBlock);
        plugin.saveConfig();
    }

    private static void initFarms(ConfigurationSection headSection){
        for(String farmName : headSection.getKeys(false)){
            ConfigurationSection fSection = getSubSection(farmName, headSection);
            Reference.log.info("Loading farm:" + fSection.getString(cfgN.FORMATTED_NAME.toString()));
            Farm f = new Farm(farmName, fSection.getString(cfgN.FORMATTED_NAME.toString(), ChatColor.AQUA + farmName));

            ConfigurationSection sesSect = getSubSection(cfgN.SIZES_SECTION, fSection);
            for(String size : sesSect.getKeys(false)){
                ConfigurationSection sSect = getSubSection(size, sesSect);
                ArrayList<ItemStack> items = loadResources(sSect);
                f.addSize(size,
                        sSect.getString(cfgN.FORMATTED_NAME.toString(), ChatColor.AQUA + size),
                        sSect.getString(cfgN.SCHEMATIC_FILE.toString(), "NoSchematicDefined.schematic"),
                        sSect.getInt(cfgN.PRICE.toString(), 10),
                        items);
                Reference.log.info("Adding size:" + size);
            }

            Reference.farmList.put(farmName, f);
        }
    }

    public static void addFarm(Farm farm){
        ConfigurationSection farmSection = getSubSection(farm.getName(), getMainSection(cfgN.FARMS_SECTION));
        ConfigurationSection sizesSection = getSubSection(cfgN.SIZES_SECTION, farmSection);
        for(Farm.Size s : farm.getSizes().values()){
            ConfigurationSection c = getSubSection(s.getName(), sizesSection);
            c.set(cfgN.FORMATTED_NAME.toString(), s.getFancyName());
            c.set(cfgN.SCHEMATIC_FILE.toString(), s.getSchemName());
            c.set(cfgN.PRICE.toString(), s.getPrice());
            saveResources(s.getResources(), c);
        }
    }

    private static void saveResources(List<ItemStack> resources, ConfigurationSection sizeSection){
        ConfigurationSection resourceSect = getSubSection(cfgN.RESOURCES_SECTION, sizeSection);
        int i = 0;
        for(ItemStack item : resources){
            resourceSect.set(String.valueOf(i), item);
            i++;
        }
    }

    private static ArrayList<ItemStack> loadResources(ConfigurationSection sizeSection){
        ConfigurationSection resourceSect = getSubSection(cfgN.RESOURCES_SECTION, sizeSection);

        ArrayList<ItemStack> list = new ArrayList<>();

        for(String s : resourceSect.getKeys(false)){
            list.add(resourceSect.getItemStack(s, new ItemStack(Material.STONE_BUTTON)));
        }
        if(list.size() <= 0){
            list.add(new ItemStack(Material.WOOD_BUTTON));
        }
        return list;
    }

    private static ConfigurationSection getMainSection(cfgN sectionName){
        return getMainSection(sectionName.toString());
    }

    private static ConfigurationSection getMainSection(String sectionName){
        ConfigurationSection section = getConfig().getConfigurationSection(cfgN.PREFIX + "." + sectionName);
        if(section == null){
            section = getConfig().createSection(cfgN.PREFIX + "." + sectionName);
        }
        return section;
    }

    private static ConfigurationSection getSubSection(cfgN sectionName, ConfigurationSection headSection){
        return getSubSection(sectionName.toString(), headSection);
    }

    private static ConfigurationSection getSubSection(String sectionName, ConfigurationSection headSection){
        ConfigurationSection section = headSection.getConfigurationSection(sectionName);
        if(section == null){
            section = headSection.createSection(sectionName);
        }
        return section;
    }

    public enum cfgN{
        PREFIX(plugin.getDescription().getPrefix()),
        FARMS_SECTION("farms"),
        GENERAL_SECTION("general"),
        FARMBLOCK("farmblock"),
        ITEM_LORE_HEADER("itemheader"),
        FORMATTED_NAME("fancyname"),
        SIZES_SECTION("sizes"),
        SCHEMATIC_FILE("schematic"),
        PRICE("price"),
        RESOURCES_SECTION("resources");

        private final String toString;

        cfgN(String configHeader){
            toString = configHeader;
        }

        @Override
        public String toString(){
            return toString;
        }
    }
}