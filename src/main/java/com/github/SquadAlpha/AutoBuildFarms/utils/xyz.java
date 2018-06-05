package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class xyz implements ConfigurationSerializable {
    private final int x, y, z;

    public xyz(Map<String, Object> map) {
        this.x = (int) map.get("x");
        this.y = (int) map.get("y");
        this.z = (int) map.get("z");
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        return map;
    }


}
