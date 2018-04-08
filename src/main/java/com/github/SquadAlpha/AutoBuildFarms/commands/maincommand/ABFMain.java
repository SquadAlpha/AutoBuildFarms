package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.commands.ABFCommand;
import com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu.MainMenu;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ABFMain extends ABFCommand {

    @Getter
    private final HashMap<String, subOption> subOptions;

    public ABFMain() {
        super("ABF", "the main " + Reference.plugin.getName() + " command", "/ABF help", "abf", "autobuildfarms");
        this.subOptions = new HashMap<>();
        this.subOptions.put("help", new helpOption(this));
        this.subOptions.put("list", new listOption(this));
        this.subOptions.put("place", new placeOption(this));
    }

    @Override
    public boolean _execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length <= 0) {
            if (this.isPlayerAndYell(sender)) {
                Player p = (Player) sender;
                openMenu(p);
                return true;
            } else {
                return false;
            }
        }
        ArrayList<String> argList = new ArrayList<>();
        Stream.of(args).forEachOrdered(arg -> argList.add(arg.toLowerCase()));
        if (argList.size() > 0 && this.subOptions.containsKey(argList.get(0))) {
            return this.subOptions.get(argList.get(0))._execute(sender, commandLabel, argList);
        } else {
            return this.subOptions.get("help")._execute(sender, commandLabel, argList);
        }
    }

    private void openMenu(Player sender) {
        MainMenu menu = new MainMenu(sender);
        menu.open();
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        String arg = args[0].toLowerCase();
        subOption opt = this.subOptions.get(arg);
        if (opt == null) {
            ArrayList<String> options = new ArrayList<>();
            subOptions.keySet().forEach(k -> {
                if (k.startsWith(arg)) {
                    options.add(k);
                }
            });
            return options;
        } else {
            return opt.onTabComplete(commandSender, command, alias, args);
        }
    }

}
