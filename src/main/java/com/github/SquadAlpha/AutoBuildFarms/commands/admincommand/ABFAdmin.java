package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandwithSubcommands;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.scheduler.Scheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;

public class ABFAdmin extends CommandwithSubcommands {

    private static final Function<onCommandArgs, Boolean> create = (a) -> {
        if (a.getArgs().length <= 1) {
            new ChatBuilder(a.getSender())
                    .append(ChatColor.RED, "No farm name specified")
                    .send();
            return false;
        } else {
            farmCreation fc = new farmCreation(a);
            Scheduler s = Schedulers.async();
            s.run(fc.go);
            return true;
        }
    };

    public ABFAdmin(AutoBuildFarms plugin) {
        super("ABFadmin", plugin);
        this.registerSubOption(new subOption(this, "create", create));
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Getter(AccessLevel.PRIVATE)
    private static class farmCreation {

        private final onCommandArgs a;//

        private final String name;//
        private final ConfigurationSection sect;//
        private final AutoBuildFarms plugin;//

        private String fancyName;//
        private ItemStack displayItem;
        @Getter
        private final Runnable go = () -> {
            AtomicBoolean canceled = new AtomicBoolean(false);
            if (!(this.getA().getSender() instanceof Player)) {
                new ChatBuilder(this.getA().getSender())
                        .append(ChatColor.RED, "You need to be a player to execute this command").newLine()
                        .append(ChatColor.YELLOW, "You are:")
                        .append(ChatColor.GOLD, this.getA().getSender().getName() + ":" + this.getA().getSender().toString())
                        .send();
            }
            CountDownLatch cdl = new CountDownLatch(1);

            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.WHITE, "Please enter the fancy name of the farm")
                    .newLine().append(ChatColor.GRAY, "color codes paragraphs replaced by &'s").send();

            Events.subscribe(AsyncPlayerChatEvent.class)
                    .filter(e -> e.getPlayer().equals(this.getA().getSender()))
                    .biHandler((sub, event) -> {
                        if (event.getMessage().equals("cancel")) {
                            canceled.set(true);
                            cdl.countDown();
                        }
                        this.fancyName = event.getMessage().replaceAll("(?<!\\\\)&", "§");
                        sub.unregister();
                        cdl.countDown();
                        event.setCancelled(true);
                    });
            try {
                cdl.await();
            } catch (InterruptedException ignored) {
            }
            if (canceled.get()) {
                sayCanceled();
                return;
            }
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.GREEN, "Name registered:")
                    .append(ChatColor.RESET, this.getFancyName()).send();
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.WHITE, "Please click the display item").send();


            ItemChooser menu = new ItemChooser((Player) this.getA().getSender(), canceled);
            this.displayItem = menu.await();
            if (canceled.get()) {
                sayCanceled();
                return;
            }

            FarmType ft = new FarmType(this.getPlugin(), this.getName(), this.getFancyName(), new ArrayList<>(), this.getDisplayItem());
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.GREEN, "Created farm:")
                    .append(ChatColor.GOLD, ft.toString()).send();
        };

        public farmCreation(onCommandArgs a) {
            this.a = a;
            this.name = this.getA().getArgs()[1];
            this.plugin = (AutoBuildFarms) this.getA().getCmd().getPlugin();
            this.sect = this.getPlugin().getConfigFile().createFarmSection(this.getName());
        }

        private void sayCanceled() {
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.YELLOW, "Farm creation canceled").send();
        }

        private static class ItemChooser extends Gui {

            private final CountDownLatch cdl;
            private final AtomicBoolean canceled;
            private ItemStack item;

            public ItemChooser(Player player, AtomicBoolean canceled) {
                super(player, 1, "Click the item that represents this farm");
                this.cdl = new CountDownLatch(1);
                this.canceled = canceled;
            }

            public ItemStack await() {
                this.open();
                try {
                    this.cdl.await();
                } catch (InterruptedException ignored) {
                }
                return this.item;
            }

            @Override
            public void redraw() {
                Map<ClickType, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
                handlers.put(ClickType.LEFT, iCE -> {
                    canceled.set(true);
                    this.cdl.countDown();
                });
                this.addItem(new Item(handlers, ItemStackBuilder.of(Material.BARRIER).name(ChatColor.RED + "Cancel").build()));
            }

            @Override
            public void open() {
                Events.subscribe(InventoryClickEvent.class)
                        .filter(e -> e.getInventory().getHolder() != null)
                        .filter(e -> e.getWhoClicked().equals(this.getPlayer()))
                        .filter(e -> !e.getCurrentItem().getType().equals(Material.AIR))
                        .filter(e -> !(e.getCurrentItem().getType().equals(Material.BARRIER) &&
                                e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel")))
                        .handler(e -> {
                            e.setCancelled(true);
                            this.item = e.getCurrentItem();
                            this.close();
                            this.cdl.countDown();
                        });
                super.open();
            }
        }

    }
}
