package com.github.SquadAlpha.AutoBuildFarms.registry;

import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.farm.PlacedFarm;
import lombok.Getter;


public class Registries {
    @Getter
    private final Registry<FarmType> FarmTypes;
    @Getter
    private final Registry<PlacedFarm> PlacedFarms;

    public Registries() {
        this.FarmTypes = new Registry<>();
        this.PlacedFarms = new Registry<>();
    }
}
