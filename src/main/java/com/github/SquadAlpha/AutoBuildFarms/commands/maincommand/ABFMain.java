package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandwithSubcommands;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmSize;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.farm.PlacedFarm;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ABFMain extends CommandwithSubcommands {
    public ABFMain(AutoBuildFarms plugin) {
        super("ABF", plugin);
        this.registerSubOption(new subOption("place", a -> {


            if (a.getArgs().length > 1 && this.getPlugin().getRegistries().getFarmTypes().searchFirst(a.getArgs()[1]) != null) {
                String sFarm = a.getArgs()[1];
                FarmType farm = this.getPlugin().getRegistries().getFarmTypes().searchFirst(sFarm);
                AtomicReference<FarmSize> size = new AtomicReference<>();
                if (a.getArgs().length > 2) {
                    String sSize = a.getArgs()[2];
                    farm.getSizes().forEach(s -> {
                        if (s.getName().contains(sSize)) {
                            size.set(s);
                        }
                    });
                    if (size.get() != null) {
                        if (size.get().canBePlacedBy(a.getSender())) {
                            //TODO check money and other things I can think off
                            PlacedFarm pf = new PlacedFarm(this.getPlugin(), size.get(), ((Player) a.getSender()).getLocation());
                            this.getPlugin().getRegistries().getPlacedFarms().add(pf);
                            pf.registerTasks();
                        } else {
                            //TODO check whats wrong or let it be handled by canBePlacedBy
                        }
                    } else {
                        invalidSometingSpiel("size", farm.getSizes(), a.getSender());
                    }
                } else {
                    invalidSometingSpiel("size", farm.getSizes(), a.getSender());
                }
            } else {
                invalidSometingSpiel("farm", this.getPlugin().getRegistries().getFarmTypes().getObjects(), a.getSender());
            }
            return true;
        }));
    }

    public static void invalidSometingSpiel(String what, List<? extends RegistryObject> options, CommandSender sender) {
        ComponentBuilder builder = new ComponentBuilder("");
        TextComponent error = new TextComponent("invalid " + what + " ");
        error.setColor(ChatColor.RED);
        builder.append(error);

        ComponentBuilder ncb = new ComponentBuilder("Possible options:").color(ChatColor.YELLOW);
        options.forEach(o -> ncb.append((o.getName().concat(", "))));
        builder.append(ncb.create());
        sender.spigot().sendMessage(builder.create());
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            sender.sendMessage("NOOP");
            return true;
        } catch (Throwable t) {
            return false;
        }
    }
}
