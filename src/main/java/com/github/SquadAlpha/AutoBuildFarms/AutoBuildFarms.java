package com.github.SquadAlpha.AutoBuildFarms;

import com.github.SquadAlpha.AutoBuildFarms.commands.ABFCommand;
import com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.ABFMain;
import com.github.SquadAlpha.AutoBuildFarms.config.Config;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Collections;
import java.util.HashMap;

import static com.github.SquadAlpha.AutoBuildFarms.reference.Reference.*;

public class AutoBuildFarms extends ExtendedJavaPlugin{

    @Override
    protected void load(){
        super.load();
    }

    @Override
    protected void enable(){
        plugin = this;
        farmList = new HashMap<>();
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
        Config.init();
        if (farmList.size() == 0) {
            log.info("No farms found in config adding default one");
            Farm f = new Farm("cactus", "&6&lCactus");
            f.addSize("small", "&bSmall", "CactusSmall.schematic", 100, Collections.singletonList(new ItemStack(Material.CACTUS, 5)));
            f.addSize("medium", "&bMedium", "CactusMedium.schematic", 500, Collections.singletonList(new ItemStack(Material.CACTUS, 25)));
            Config.addFarm(f);
            Config.save();
            Config.reload();
            Config.init();
        }
        this.registerListener(new Listeners());
        this.easyreg(new ABFMain());
        super.enable();
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

    private <T extends ABFCommand> T easyreg(T cmd) {
        return this.registerCommand(cmd, cmd.getAliases().toArray(new String[0]));
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @Override
    protected void disable(){
        Config.save();
        super.disable();
    }

}
