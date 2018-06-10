package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@Data
public class xyz implements ConfigurationSerializable {
    private final int x, y, z;
    private final Vector vector;

    public xyz(Map<String, Object> map) {
        this.x = (int) map.get("x");
        this.y = (int) map.get("y");
        this.z = (int) map.get("z");
        this.vector = new Vector(x, y, z);
    }

    public xyz(Location l) {
        this.x = l.getBlockX();
        this.y = l.getBlockY();
        this.z = l.getBlockZ();
        this.vector = new Vector(x, y, z);
    }

    public xyz(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vector = new Vector(this.x, this.y, this.z);
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
