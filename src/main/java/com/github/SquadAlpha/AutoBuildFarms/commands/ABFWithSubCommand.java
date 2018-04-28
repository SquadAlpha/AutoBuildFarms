package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.subOption;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public abstract class ABFWithSubCommand extends ABFCommand {

    @Getter
    private HashMap<String, subOption> subOptions;

    protected ABFWithSubCommand(String name, String description, String usageMessage, String... aliases) {
        super(name, description, usageMessage, aliases);
        this.subOptions = new HashMap<>();

    }

    protected void addSubOption(subOption opt) {
        this.subOptions.put(opt.getName().toLowerCase(), opt);
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

    @Override
    public boolean _execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length <= 0) {
            return this.onMainExecute(sender, commandLabel, args);
        }
        ArrayList<String> argList = new ArrayList<>();
        Stream.of(args).forEachOrdered(arg -> argList.add(arg.toLowerCase()));
        if (argList.size() > 0 && this.subOptions.containsKey(argList.get(0))) {
            return this.subOptions.get(argList.get(0))._execute(sender, commandLabel, argList);
        } else {
            return this.subOptions.get("help")._execute(sender, commandLabel, argList);
        }
    }

    protected abstract boolean onMainExecute(CommandSender sender, String commandLabel, String[] args);

}
