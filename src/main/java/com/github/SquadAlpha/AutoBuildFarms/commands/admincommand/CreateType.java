package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.ItemChooser;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import lombok.AccessLevel;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class CreateType {
    static final Function<onCommandArgs, Boolean> func = (a) -> {
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
                return;
            }
            CountDownLatch cdl = new CountDownLatch(1);

            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.WHITE, "Please enter the fancy name of the farm")
                    .newLine().append(ChatColor.GRAY, "color code paragraphs replaced by &'s").send();

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


            ItemChooser menu = new ItemChooser((Player) this.getA().getSender(), "Click the item that represents this farm", canceled);
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

    }

}
