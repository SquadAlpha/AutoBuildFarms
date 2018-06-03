package com.github.SquadAlpha.AutoBuildFarms;

import com.github.SquadAlpha.AutoBuildFarms.file.Config;
import com.github.SquadAlpha.AutoBuildFarms.file.DataFile;
import com.github.SquadAlpha.AutoBuildFarms.registry.Registries;
import com.github.SquadAlpha.AutoBuildFarms.utils.Defaults;
import com.github.SquadAlpha.AutoBuildFarms.utils.ErrorHandling;
import com.github.SquadAlpha.AutoBuildFarms.utils.numberItem;
import lombok.Getter;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.logging.Logger;


public class AutoBuildFarms extends ExtendedJavaPlugin {
    @Getter
    private Config configFile;
    @Getter
    private Logger log;
    //@Getter private static AutoBuildFarms plugin;
    @Getter
    private DataFile dataFile;
    @Getter
    private Registries registries;
    @Getter
    private ErrorHandling eh;

    @Override
    protected void load() {
        super.load();
    }

    @Override
    protected void enable() {
        //AutoBuildFarms.plugin = this;
        this.eh = new ErrorHandling(this);
        ConfigurationSerialization.registerClass(numberItem.class);
        this.registries = new Registries();
        this.configFile = new Config(this);
        this.log = super.getLogger();
        this.dataFile = new DataFile(this);
        this.setDefaultFarms();
        super.enable();
    }

    private void setDefaultFarms() {
        if (this.registries.getFarmTypes().getObjects().size() <= 0) {
            this.log.info("Creating default farms");
            Defaults.createDefaultFarms(this);
        }
    }

    @Override
    protected void disable() {
        boolean b;
        b = this.eh.NPE(() -> this.dataFile.save(), this.log);
        if (!b) {
            this.log.warning("Failed to save data file");
        }
        b = this.eh.NPE(() -> this.configFile.save(), this.log);
        if (!b) {
            this.log.warning("Failed to save config file");
        }
        super.disable();
    }
}
