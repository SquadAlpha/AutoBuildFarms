package com.github.SquadAlpha.AutoBuildFarms.reference;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.logging.Logger;

public class Reference{
    public static AutoBuildFarms plugin;
    public static Permission perms;
    public static Economy econ;
    public static Logger log;

    public static HashMap<String, Farm> farmList;
    public static String loreHeader;
    public static ItemStack farmBlock;
}
