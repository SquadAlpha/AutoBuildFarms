package com.github.SquadAlpha.AutoBuildFarms.eventhandling;


import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import me.lucko.helper.Events;
import me.lucko.helper.event.SingleSubscription;
import me.lucko.helper.event.Subscription;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Listeners {


    private static ArrayList<SingleSubscription> al;

    public static void init() {
        al = new ArrayList<>();
        // when clicked of farm
        al.add(Events.subscribe(PlayerInteractEvent.class)
                .filter(pie -> pie.getClickedBlock().getType().equals(Material.CHEST))
                .filter(pie -> {
                    for (PlacedFarm f : Reference.placedFarms) {
                        if (pie.getClickedBlock().getLocation().equals(f.getChest().getLocation())) {
                            if (f.canBeOpenedBy(pie.getPlayer())) {
                                return true;
                            } else {
                                pie.setCancelled(true);
                            }
                        }
                    }
                    return false;
                })
                .handler(new PIEHandler()));
    }

    public static void destroy() {
        al.forEach(Subscription::close);
    }


    private static class PIEHandler implements Consumer<PlayerInteractEvent> {

        @Override
        public void accept(PlayerInteractEvent playerInteractEvent) {
            //TODO write this to open gui
        }
    }
}
