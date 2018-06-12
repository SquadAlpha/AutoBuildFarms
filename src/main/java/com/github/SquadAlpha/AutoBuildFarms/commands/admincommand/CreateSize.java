package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.input.*;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import lombok.AccessLevel;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Scheduler;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
        private String schematicFile;
        private xyz chestOffset;

        private ItemStack displayItem;
        private ArrayList<ItemStack> materials;
        private ArrayList<ItemStack> revenue;


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

                PlayerInput<String> sc = new StringInput((Player) this.getA().getSender(),
                        "Please enter the fancy name of this size",
                        "Fancy name registered:%1",
                        "Fancy name setting failed",this.canceled);
                sc.start();
                this.fancyName = sc.await();

                if (canceled.get()) {
                    sayCanceled();
                    return;
                }

                PlayerInput<Double> pr = new DoubleInput((Player) this.getA().getSender(),
                        "Please enter the price "+ ChatColor.RESET+this.getFancyName()+ChatColor.RESET+" costs",
                        "Price set to %1"+
                        ((AutoBuildFarms) this.getA().getCmd().getPlugin()).getEcon().currencyNamePlural(),
                        "Price setting failed",this.canceled);
                pr.start();
                this.price = pr.await();
                if (canceled.get()) {
                    sayCanceled();
                    return;
                }

                PlayerInput<String> sf = new StringInput((Player) this.getA().getSender(),
                        "Please enter the name of the schematic file including the .schematic extension",
                        "Set the schematic file to:%1",
                        "Schematic file setting failed",this.canceled);
                sf.start();
                this.schematicFile = sf.await();
                if (canceled.get()) {
                    sayCanceled();
                    return;
                }

                PlayerInput<xyz> co = new xyzInput((Player) this.getA().getSender(),
                        "Please enter the location of the chest relative to the schematic",
                        "Set the chest offset location to:%1",
                        "Canceled setting the chestoffset",this.canceled);
                co.start();
                this.chestOffset = co.await();

                PlayerInput<ItemStack> di = new ItemInput((Player) this.getA().getSender(),
                        "Click the item that represents this size",
                        "Item successfully set to %1",
                        "display item setting canceled",canceled);
                di.start();
                this.displayItem = di.await();
                if (canceled.get()) {
                    sayCanceled();
                    return;
                }
                this.materials = new ArrayList<>();
                int i = 1;
                AtomicBoolean matCanceled = new AtomicBoolean(false);
                do{
                    PlayerInput<ItemStack> pi = new ItemInput((Player) this.getA().getSender(),
                            "Please select the "+i+"th construction material stack",
                            "Set material "+i+" to %1",
                            "Done setting materials",matCanceled);
                    pi.start();
                    i++;
                    ItemStack mat = pi.await();
                    if(!matCanceled.get()) {
                        this.materials.add(mat);
                    }
                }while (!matCanceled.get());

                //TODO revenue (Item selection and number parsing combined in a loop)
            };
        }

        private void sayCanceled() {
            new ChatBuilder(this.getA().getSender())
                    .append(ChatColor.YELLOW, "Size creation canceled").send();
        }
    }
}
