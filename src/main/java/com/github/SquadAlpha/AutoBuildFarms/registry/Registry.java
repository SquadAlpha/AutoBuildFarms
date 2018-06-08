package com.github.SquadAlpha.AutoBuildFarms.registry;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class Registry<T extends RegistryObject> {
    @Getter
    private final ArrayList<T> objects;

    public Registry() {
        objects = new ArrayList<>();
    }

    public T get(String name) {
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

    public boolean addAll(Collection<T> l) {
        return this.objects.addAll(l);
    }

    public ArrayList<T> search(String name) {
        ArrayList<T> res = new ArrayList<>();
        for (T o : this.getObjects()) {
            if (o.getName().contains(name)) {
                res.add(o);
            }
        }
        return res;
    }

    public T searchFirst(String name) {
        for (T o : this.getObjects()) {
            if (o.getName().startsWith(name)) {
                return o;
            }
        }
        return null;
    }

    public boolean remove(T o) {
        return objects.remove(o);
    }

    public void forEach(Consumer<? super T> c) {
        this.getObjects().forEach(c);
    }

}
