package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
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
    }

    @Override
    public boolean _execute(CommandSender sender, String commandLabel, String[] args) {
        if (args.length <= 0 && this.isPlayerAndYell(sender)) {
            Player p = (Player) sender;
            openMenu(p);
            return true;
        }
        ArrayList<String> argList = new ArrayList<>();
        Stream.of(args).forEach(arg -> argList.add(arg.toLowerCase()));
        if (this.subOptions.containsKey(argList.get(0))) {
            return this.subOptions.get(argList.get(0))._execute(sender, commandLabel, argList);
        } else {
            return this.subOptions.get("help")._execute(sender, commandLabel, argList);
        }
    }

    private void openMenu(Player sender) {

    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        subOption opt = this.subOptions.get(args[0].toLowerCase());
        if (opt == null) {
            return null;
        } else {
            return opt.onTabComplete(commandSender, command, alias, args);
        }
    }

    private abstract class subOption {
        @Getter
        private final String name;
        @Getter
        private final String helpText;
        protected final ABFMain parent;
        @Getter
        private final String permission;

        protected subOption(ABFMain abfMain, String name, String helpText) {
            this.name = name;
            this.helpText = helpText;
            this.parent = abfMain;
            this.permission = Reference.plugin.getName().toLowerCase() + "." + this.parent.getName() + "." + this.name;
        }

        public final boolean _execute(CommandSender sender, String label, ArrayList<String> args) {
            if (Reference.perms.has(sender, this.getPermission())) {
                return this.execute(sender, label, args);
            } else {

                ChatBuilder cb = new ChatBuilder(sender, ChatColor.RED);
                cb.append(ChatColor.RED, "You don't have permission:").append(ChatColor.AQUA, this.getPermission());
                cb.append(ChatColor.RED, " to exectute this command");
                return false;
            }
        }

        public abstract boolean execute(CommandSender sender, String label, ArrayList<String> args);

        public abstract List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args);
    }

    private class helpOption extends subOption {
        protected helpOption(ABFMain abfMain) {
            super(abfMain, "help", "shows the help of all subcommands");
        }

        @Override
        public boolean execute(CommandSender sender, String label, ArrayList<String> args) {
            ChatBuilder builder = new ChatBuilder(sender, ChatColor.WHITE);
            builder.append(ChatColor.YELLOW, "====ABF Help====");
            for (subOption opt : this.parent.subOptions.values()) {
                builder.newLine(ChatColor.WHITE).append(ChatColor.WHITE, "/abf ").append(ChatColor.AQUA, opt.getName())
                        .append(ChatColor.YELLOW, " = ").append(ChatColor.WHITE, opt.getHelpText());
            }
            builder.send();
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
            return null;
        }
    }
}
