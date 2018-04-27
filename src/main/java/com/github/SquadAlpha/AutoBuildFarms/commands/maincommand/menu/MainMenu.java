package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MainMenu extends Gui {


    public MainMenu(Player player) {
        super(player, 1, "&6[&4A&3B&2F&1]");
    }


    @Override
    public void redraw() {
        //TODO add items
        ItemStackBuilder placechest = ItemStackBuilder.of(new ItemStack(Material.CHEST))
                .amount(1)
                .name("&2Place")
                .lore("Allows you to place the beginnings of a farm");

        this.addItem(new clickItem(placechest.build())
                .addhandler(ClickType.LEFT, e -> {
                    Player p = (Player) e.getWhoClicked();
                    PlaceMenu pm = PlaceMenu.get(p, this);
                    pm.open();
                })
                .get());

    }

    public static class clickItem {

        private final ItemStack item;
        private HashMap<ClickType, Consumer<InventoryClickEvent>> handlers;

        public clickItem(@Nonnull ItemStack itemStack) {
            this.item = itemStack;
            this.handlers = new HashMap<>();
        }

        public clickItem addhandler(ClickType c, Consumer<InventoryClickEvent> h) {
            this.handlers.put(c, h);
            return this;
        }

        public rClickItem get() {
            return new rClickItem(handlers, item);
        }

        public class rClickItem extends Item {

            public rClickItem(@Nonnull Map<ClickType, Consumer<InventoryClickEvent>> handlers, @Nonnull ItemStack itemStack) {
                super(handlers, itemStack);
            }
        }
    }
}
