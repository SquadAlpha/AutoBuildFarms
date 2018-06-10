package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ItemInput extends Gui implements PlayerInput<ItemStack> {

    private ItemStack item;

    private final CountDownLatch cdl;
    private final AtomicBoolean canceled;
    private SingleSubscription<InventoryClickEvent> sub;

    public ItemInput(Player player, String title, AtomicBoolean canceled) {
        super(player, 1, title);
        this.canceled = canceled;
        this.cdl = new CountDownLatch(1);
    }

    protected final void done(){
        this.cdl.countDown();
    }

    protected final void waitUntilDone(){
        try {
            this.cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemStack await(){
        this.waitUntilDone();
        this.sub.unregister();
        return this.item;
    }

    @Override
    public void go(){
        new ChatBuilder(this.getPlayer()).append(ChatColor.WHITE,this.getInitialTitle()).send();
        this.open();
    }

    @Override
    public void redraw() {
        Map<ClickType, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
        handlers.put(ClickType.LEFT, iCE -> {
            canceled.set(true);
            this.done();
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
                    this.item = e.getCurrentItem();
                    this.close();
                    this.done();
                });
        super.open();
    }

}
