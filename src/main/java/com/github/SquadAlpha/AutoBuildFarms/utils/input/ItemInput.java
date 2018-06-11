package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import me.lucko.helper.Events;
import me.lucko.helper.event.SingleSubscription;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ItemInput extends PlayerInput<ItemStack> {

    private final Menu menu;


    public ItemInput(Player player, String requestText, String successText, String cancelText, AtomicBoolean canceled) {
        super(player, requestText, successText, cancelText, canceled);
        this.menu = new Menu(player,ChatColor.AQUA+requestText,this);
    }

    @Override
    protected void go() {
     this.menu.open();
    }

    private static class Menu extends Gui{

        private SingleSubscription<InventoryClickEvent> sub;
        private final ItemInput parent;

        public Menu(Player player, String title, ItemInput parent) {
            super(player, 1, title);
            this.parent = parent;
        }

        @Override
        public void redraw() {
            Map<ClickType, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
            handlers.put(ClickType.LEFT, iCE -> {
                this.parent.cancel();
                this.close();
            });
            this.addItem(new Item(handlers, ItemStackBuilder.of(Material.BARRIER).name(ChatColor.RED + "Cancel").build()));
        }

        @Override
        public void open() {
            this.sub = Events.subscribe(InventoryClickEvent.class)
                    .filter(e -> e.getInventory().getHolder() != null)
                    .filter(e -> e.getWhoClicked().equals(this.getPlayer()))
                    .filter(e -> !e.getCurrentItem().getType().equals(Material.AIR))
                    .filter(e -> !(e.getCurrentItem().getType().equals(Material.BARRIER) &&
                            e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel")))
                    .handler(e -> {
                        e.setCancelled(true);
                        this.parent.setAnswer(e.getCurrentItem());
                        this.close();
                        this.sub.unregister();
                    });
            super.open();
        }
    }


}
