package com.github.SquadAlpha.AutoBuildFarms;

import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

import static com.github.SquadAlpha.AutoBuildFarms.Reference.*;

public class AutoBuildFarms extends ExtendedJavaPlugin{

    @Override
    protected void load(){
        super.load();
    }

    @Override
    protected void enable(){
        plugin = this;
        log = getLogger();
        if(!setupPermissions()){
            log.severe(String.format("[%s] - Disabled due to no Vault Permissions dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        if(!setupEconomy()){
            log.severe(String.format("[%s] - Disabled due to no Vault Economy dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    @Override
    protected void disable(){
        super.disable();
    }
}
