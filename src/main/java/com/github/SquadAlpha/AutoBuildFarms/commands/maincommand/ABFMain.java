package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandWithSubCommands;
import com.github.SquadAlpha.AutoBuildFarms.registry.RegistryObject;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;


public class ABFMain extends CommandWithSubCommands {

    public ABFMain(AutoBuildFarms plugin) {
        super("ABF", plugin);
        this.registerSubOption(new SubOption(this, "place", Place.getFunc(), Place.getTabComplete()));
        this.registerSubOption(new SubOption(this, "list",
                com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.List.getFunc(),
                com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.List.getTabComplete()));
    }

    public static void invalidSomethingSpiel(String what, List<? extends RegistryObject> options, CommandSender sender) {
        ChatBuilder cb = new ChatBuilder(sender)
                .append(ChatColor.RED, "invalid " + what + " ")
                .append(ChatColor.YELLOW, "Possible options:");
        options.forEach(o -> cb.append(ChatColor.GOLD, o.getName().concat(", ")));
        cb.send();
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            sender.sendMessage("TODO Design menu");
            //TODO design menu
            return true;
        } catch (Throwable t) {
            return false;
        }
    }

}
