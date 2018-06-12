package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class numberItem implements ConfigurationSerializable {
    private final long num;
    private final ItemStack item;

    public numberItem(Map<String, Object> map){

        Object num = map.get("num");
        this.item = (ItemStack) map.get("item");
        if (num instanceof Long) {
            this.num = (long) num;
        } else if (num instanceof Integer) {
            this.num = ((Integer) num).longValue();
        } else {
            long num1;
            try {
                num1 = Long.valueOf(String.valueOf(num));
            } catch (ClassCastException e) {
                e.printStackTrace();
                num1 = 20;
            }
            this.num = num1;
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
