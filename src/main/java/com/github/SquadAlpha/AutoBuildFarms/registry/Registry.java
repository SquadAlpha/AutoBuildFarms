package com.github.SquadAlpha.AutoBuildFarms.registry;

import lombok.Getter;

import java.util.ArrayList;

public class Registry<T extends RegistryObject> {
    @Getter
    private final ArrayList<T> objects;

    public Registry() {
        objects = new ArrayList<>();
    }

    public T lookupName(String name) {
        for (T o : objects) {
            if (o.getName().equals(name)) {
                return o;
            }
        }
        return null;
    }

    public boolean add(T o) {
        if (!objects.contains(o)) {
            objects.add(o);
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(T o) {
        return objects.remove(o);
    }
}
