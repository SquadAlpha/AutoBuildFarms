package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class numberItem implements ConfigurationSerializable {
    private final long num;
    private final ItemStack item;

    public numberItem(ConfigurationSection sect) {
        this.num = sect.getLong("num", 10);
        this.item = sect.getItemStack("item", ItemStackBuilder.of(Material.STICK).name("Error Loading " + sect.getCurrentPath()).build());
    }

    public static numberItem deserialize(Map<String, Object> map) {
        Object num = map.get("num");
        ItemStack stack = (ItemStack) map.get("item");
        if (num instanceof Long) {
            return new numberItem((Long) num, stack);
        } else if (num instanceof Integer) {
            long i = ((Integer) num).longValue();
            return new numberItem(i, stack);
        } else {
            try {
                return new numberItem(Long.valueOf(String.valueOf(num)), stack);
            } catch (ClassCastException e) {
                e.printStackTrace();
                return new numberItem(20, stack);
            }
        }
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("num", this.num);
        map.put("item", this.item);
        return map;
    }

}
