package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import lombok.AccessLevel;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class CreateSize {
    static final Function<onCommandArgs, Boolean> func = (a) -> {
        if (a.getArgs().length <= 1) {
            new ChatBuilder(a.getSender())
                    .append(ChatColor.RED, "No farm name specified")
                    .send();
            return false;
        } else if(a.getArgs().length <= 2) {
            new ChatBuilder(a.getSender())
                    .append(ChatColor.RED, "No size name specified")
                    .send();
            return false;
        } else {
            CreateSize.SizeCreation fc = new SizeCreation(a);
            Scheduler s = Schedulers.async();
            s.run(fc.go);

            return true;
        }
    };

    @Getter(AccessLevel.PRIVATE)
    private static class SizeCreation {
        private final onCommandArgs a;
        public final Runnable go;
        private final AtomicBoolean canceled;

        private final String name;
        private final FarmType parent;

        private String fancyName;
        private double price;
        private String schemfile;
        private xyz chestOffset;

        private ItemStack displayItem;
        private ArrayList<ItemStack> materials;
        private ArrayList<ItemStack> revenue;

        /*
        map.put("item", this.displayItem);
        map.put("materials", this.materials);
        map.put("revenue", this.revenue);
         */

        public SizeCreation(onCommandArgs a) {
            this.canceled = new AtomicBoolean(false);
            this.a = a;
            this.name = a.getArgs()[2];
            this.parent = ((AutoBuildFarms)a.getCmd().getPlugin()).getRegistries().getFarmTypes().searchFirst(a.getArgs()[1]);
            if(this.getParent()==null){
                ChatBuilder cb = new ChatBuilder(a.getSender())
                        .append(ChatColor.RED, "Farm:")
                        .append(ChatColor.RESET, a.getArgs()[1])
                        .append(ChatColor.RED, " not found").newLine()
                        .append(ChatColor.YELLOW, "Please use one of:");
                ((AutoBuildFarms)a.getCmd().getPlugin()).getRegistries().getFarmTypes()
                        .forEach(ft -> cb.append(ChatColor.WHITE,ft.getName())
                                .append(ChatColor.GOLD,", "));
                cb.send();
                this.canceled.set(true);
            }
            this.go = () -> {
                if(this.canceled.get()){ sayCanceled();return;}
                if (!(this.getA().getSender() instanceof Player)) {
                    new ChatBuilder(this.getA().getSender())
                            .append(ChatColor.RED, "You need to be a player to execute this command").newLine()
                            .append(ChatColor.YELLOW, "You are:")
                            .append(ChatColor.GOLD, this.getA().getSender().getName() + ":" + this.getA().getSender().toString()).send();return;
                }

                CountDownLatch cdl = new CountDownLatch(1);

                new ChatBuilder(this.getA().getSender())
                        .append(ChatColor.WHITE, "Please enter the fancy name of the size")
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


                //TODO price
                //TODO schemfile
                //TODO chestOffset


                //TODO display item
                //TODO materials

                //TODO revenue (Item selection and number parsing)
            };
        }

        private void sayCanceled() {
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.YELLOW, "Size creation canceled").send();
        }
    }
}
