package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import com.github.SquadAlpha.AutoBuildFarms.config.Config;
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
        super(player, 1, Config.getMainMenuTitle());
    }


    @Override
    public void redraw() {
        //TODO add items
        this.addItem(new clickItem(new ItemStack(Material.CHEST, 1))
                .addhandler(ClickType.LEFT, e -> {
                    Player p = (Player) e.getWhoClicked();
                    PlaceMenu pm = new PlaceMenu(p, this);
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
