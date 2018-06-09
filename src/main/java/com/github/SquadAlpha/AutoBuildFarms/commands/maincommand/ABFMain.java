package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandwithSubcommands;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmSize;
import com.github.SquadAlpha.AutoBuildFarms.farm.FarmType;
import com.github.SquadAlpha.AutoBuildFarms.farm.PlacedFarm;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.onCommandArgs;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Function;


public class ABFMain extends CommandwithSubcommands {

    private static void invalidSometingSpiel(String what, List<? extends RegistryObject> options, CommandSender sender) {
        ChatBuilder cb = new ChatBuilder(sender)
                .append(ChatColor.RED, "invalid " + what + " ")
                .append(ChatColor.YELLOW, "Possible options:");
        options.forEach(o -> cb.append(ChatColor.GOLD, o.getName().concat(", ")));
        cb.send();
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            sender.sendMessage("NOOP");
            //TODO design menu
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

    //Start suboptions
    @SuppressWarnings("FieldCanBeLocal")
    private final Function<onCommandArgs, Boolean> list = a -> {
        ChatBuilder cb = new ChatBuilder(a.getSender())
                .append(ChatColor.GOLD, "Farms:");
        this.getPlugin().getRegistries().getFarmTypes().forEach(ft -> {

            cb.newLine().append(ChatColor.GOLD, " ")
                    .append(ChatColor.RESET, ft.getFancyName())
                    .append(ChatColor.GOLD, ":")
                    .append(ChatColor.WHITE, ft.getName());
            ft.getSizes().forEach(fs -> cb.newLine().append(ChatColor.GOLD, "  ")
                    .append(ChatColor.RESET, fs.getFancyName())
                    .append(ChatColor.GOLD, ":")
                    .append(ChatColor.AQUA, this.getPlugin().getEcon().format(fs.getPrice()))
                    .append(ChatColor.WHITE, " " + fs.getName()));
        });
        cb.send();
        return true;
    };

    @SuppressWarnings("FieldCanBeLocal")
    private final Function<onCommandArgs, Boolean> place = a -> {


        if (a.getArgs().length > 1 && this.getPlugin().getRegistries().getFarmTypes().searchFirst(a.getArgs()[1]) != null) {
            String sFarm = a.getArgs()[1];
            FarmType farm = this.getPlugin().getRegistries().getFarmTypes().searchFirst(sFarm);
            if (a.getArgs().length > 2) {
                String sSize = a.getArgs()[2];
                FarmSize size = farm.getSizes().searchFirst(sSize);
                if (size != null) {
                    if (size.canBePlacedByAndYell(a.getSender())) {
                        PlacedFarm pf = new PlacedFarm(this.getPlugin(), size, ((Player) a.getSender()).getLocation());
                        this.getPlugin().getRegistries().getPlacedFarms().add(pf);
                        pf.registerTasks();
                        new ChatBuilder(a.getSender())
                                .append(ChatColor.GREEN, "Placed: ")
                                .append(ChatColor.GOLD, pf.getSize().getParent().getFancyName())
                                .append(ChatColor.GOLD, " ")
                                .append(ChatColor.GOLD, pf.getSize().getFancyName())
                                .append(ChatColor.GREEN, " successfully")
                                .send();
                        size.getSchem().place(((Player) a.getSender()).getLocation());//TODO do building and stuff
                    }
                } else {
                    invalidSometingSpiel("size", farm.getSizes().getObjects(), a.getSender());
                }
            } else {
                invalidSometingSpiel("size", farm.getSizes().getObjects(), a.getSender());
            }
        } else {
            invalidSometingSpiel("farm", this.getPlugin().getRegistries().getFarmTypes().getObjects(), a.getSender());
        }
        return true;
    };

    public ABFMain(AutoBuildFarms plugin) {
        super("ABF", plugin);
        this.registerSubOption(new subOption(this, "place", this.place));
        this.registerSubOption(new subOption(this, "list", this.list));
    }

}
