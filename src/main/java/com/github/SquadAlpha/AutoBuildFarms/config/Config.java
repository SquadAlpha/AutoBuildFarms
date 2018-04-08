package com.github.SquadAlpha.AutoBuildFarms.config;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import com.github.SquadAlpha.AutoBuildFarms.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.github.SquadAlpha.AutoBuildFarms.reference.Reference.*;

public class Config{


    private static FileConfiguration getConfig(){
        return plugin.getConfig();
    }

    public static void init(){
        getConfig().options().copyHeader(true);
        getConfig().options().copyDefaults(true);
        deAndify();
        initFarms(getMainSection(cfgN.FARMS_SECTION));
        initGeneral(getMainSection(cfgN.GENERAL_SECTION));
        initLang(getMainSection(cfgN.LANG_SECTION));
    }

    //Replaces most unescaped & with §
    public static void deAndify() {
        getConfig().getValues(true).forEach((k, v) -> {
            //Simple string replace
            if (v instanceof String) {
                if (((String) v).contains("&")) {
                    String nv = Utils.replaceWithParagraph("&", (String) v);
                    getConfig().set(k, nv);
                    log.fine("Set:" + k + " = " + v + " to " + nv);
                }
            } else if (v instanceof ArrayList && ((ArrayList) v).size() > 0 && ((ArrayList) v).get(0) instanceof String) {
                ArrayList<String> tv = (ArrayList<String>) v;
                ArrayList<String> nv = new ArrayList<>();
                final int[] i = {0};
                tv.forEach(s -> {
                    if (s.contains("&")) {
                        String ns = Utils.replaceWithParagraph("&", s);
                        nv.add(ns);
                        log.fine("Set:" + k + "[" + i[0] + "] = " + ns);
                    } else {
                        nv.add(s);
                    }
                    i[0]++;
                });
                getConfig().set(k, nv);
            }
        });
    }

    private static void initGeneral(ConfigurationSection configurationSection){
        Reference.farmBlock = configurationSection.getItemStack(cfgN.FARMBLOCK.toString(), new ItemStack(Material.CHEST));
        getSchematicsDir();
    }

    private static void initLang(ConfigurationSection lang) {
        Reference.loreHeader = lang.getString(cfgN.ITEM_LORE_HEADER.toString(), "&6&k|&r&6AutoFarm&k|&r");
        lang.getString(cfgN.MAIN_MENUTITLE.toString(), getMainMenuTitle());
        lang.getString(cfgN.PLACE_MENUTITLE.toString(), getPlaceMenuTitle());
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
                sizeSect.set(cfgN.FARM_DISPLAYITEM.toString(), s.getDisplayItem());
                saveMaterials(s.getMaterials(), sizeSect);
            }
        }

        //GENERAL
        ConfigurationSection general = getMainSection(cfgN.GENERAL_SECTION);
        general.set(cfgN.FARMBLOCK.toString(), Reference.farmBlock);
        general.set(cfgN.SCHEMATICS_DIR.toString(), getSchematicsDirName());
        //LANG
        ConfigurationSection lang = getMainSection(cfgN.LANG_SECTION);
        lang.set(cfgN.ITEM_LORE_HEADER.toString(), Reference.loreHeader);
        lang.set(cfgN.MAIN_MENUTITLE.toString(), getMainMenuTitle());
        lang.set(cfgN.PLACE_MENUTITLE.toString(), getPlaceMenuTitle());
        deAndify();
        plugin.saveConfig();
    }

    private static void initFarms(ConfigurationSection headSection){
        for(String farmName : headSection.getKeys(false)){
            ConfigurationSection fSection = getSubSection(farmName, headSection);
            Reference.log.fine("Loading farm:" + fSection.getString(cfgN.FORMATTED_NAME.toString()));
            Farm f = new Farm(farmName, fSection.getString(cfgN.FORMATTED_NAME.toString(), ChatColor.AQUA + farmName), fSection.getItemStack(cfgN.FARM_DISPLAYITEM.toString()));

            ConfigurationSection sesSect = getSubSection(cfgN.SIZES_SECTION, fSection);
            for(String size : sesSect.getKeys(false)){
                ConfigurationSection sSect = getSubSection(size, sesSect);
                ArrayList<ItemStack> items = loadMaterials(sSect);
                f.addSize(size,
                        sSect.getString(cfgN.FORMATTED_NAME.toString(), ChatColor.AQUA + size),
                        sSect.getString(cfgN.SCHEMATIC_FILE.toString(), "NoSchematicDefined.schematic"),
                        sSect.getInt(cfgN.PRICE.toString(), 10),
                        sSect.getItemStack(cfgN.FARM_DISPLAYITEM.toString(), new ItemStack(Material.STICK, 1)),
                        items);
                Reference.log.fine("Adding size:" + size);
            }

            Reference.farmList.put(farmName, f);
        }
    }

    public static void addFarm(Farm farm){
        ConfigurationSection farmSection = getSubSection(farm.getName(), getMainSection(cfgN.FARMS_SECTION));
        farmSection.set(cfgN.FORMATTED_NAME.toString(), farm.getFancyName());
        farmSection.set(cfgN.FARM_DISPLAYITEM.toString(), farm.getDisplayItem());
        ConfigurationSection sizesSection = getSubSection(cfgN.SIZES_SECTION, farmSection);
        for(Farm.Size s : farm.getSizes().values()){
            ConfigurationSection c = getSubSection(s.getName(), sizesSection);
            c.set(cfgN.FORMATTED_NAME.toString(), s.getFancyName());
            c.set(cfgN.SCHEMATIC_FILE.toString(), s.getSchemName());
            c.set(cfgN.PRICE.toString(), s.getPrice());
            c.set(cfgN.FARM_DISPLAYITEM.toString(), s.getDisplayItem());
            saveMaterials(s.getMaterials(), c);
        }
    }

    private static void saveMaterials(List<ItemStack> resources, ConfigurationSection sizeSection) {
        ConfigurationSection resourceSect = getSubSection(cfgN.MATERIALS_SECTION, sizeSection);
        int i = 0;
        for(ItemStack item : resources){
            resourceSect.set(String.valueOf(i), item);
            i++;
        }
    }

    private static ArrayList<ItemStack> loadMaterials(ConfigurationSection sizeSection) {
        ConfigurationSection resourceSect = getSubSection(cfgN.MATERIALS_SECTION, sizeSection);

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

    public static File getSchematicsDir() {
        String path = plugin.getDataFolder().getAbsolutePath() + File.separator + getSchematicsDirName();
        File f = new File(path);
        f.mkdirs();
        return f;
    }

    public static String getSchematicsDirName() {
        return getMainSection(cfgN.GENERAL_SECTION).getString(cfgN.SCHEMATICS_DIR.toString(), "schematics");
    }

    public static String getMainMenuTitle() {
        return getMainSection(cfgN.LANG_SECTION).getString(cfgN.MAIN_MENUTITLE.toString(), "&6Auto &bBuild &4Farms");
    }

    public static String getPlaceMenuTitle() {
        return getMainSection(cfgN.LANG_SECTION).getString(cfgN.PLACE_MENUTITLE.toString(), "[&6A&bB&4F]Place");
    }

    public enum cfgN{
        PREFIX(plugin.getDescription().getPrefix()),
        FARMS_SECTION("farms"),
        FARM_DISPLAYITEM("displayitem"),
        GENERAL_SECTION("general"),
        FARMBLOCK("farmblock"),
        FORMATTED_NAME("fancyname"),
        ITEM_LORE_HEADER("itemheader"),
        SIZES_SECTION("sizes"),
        SCHEMATIC_FILE("schematic"),
        PRICE("price"),
        MATERIALS_SECTION("materials"),
        SCHEMATICS_DIR("schematicdirectory"),
        LANG_SECTION("language"),
        MAIN_MENUTITLE("mainmenutitle"),
        PLACE_MENUTITLE("placementmenutitle");

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