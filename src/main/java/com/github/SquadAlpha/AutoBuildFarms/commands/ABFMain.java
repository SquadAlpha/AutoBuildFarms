package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (args.length <= 0 && this.isPlayerAndYell(sender)) {
            Player p = (Player) sender;
            openMenu(p);
            return true;
        }
        ArrayList<String> argList = new ArrayList<>();
        Stream.of(args).forEachOrdered(arg -> argList.add(arg.toLowerCase()));
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
            this.permission = this.parent.getPermission() + "." + this.name;
        }

        public final boolean _execute(CommandSender sender, String label, ArrayList<String> args) {
            if (Reference.perms.has(sender, this.getPermission())) {
                return this.execute(sender, label, args);
            } else {

                ChatBuilder cb = new ChatBuilder(sender, ChatColor.RED);
                cb.append(ChatColor.RED, "You don't have permission:").append(ChatColor.AQUA, this.getPermission());
                cb.append(ChatColor.RED, " to exectute this command");
                cb.send();
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

    private class listOption extends subOption {
        public listOption(ABFMain abfMain) {
            super(abfMain, "list", "list the available farms");
        }

        @Override
        public boolean execute(CommandSender sender, String label, ArrayList<String> args) {
            ChatBuilder buider = new ChatBuilder(sender, ChatColor.WHITE);
            buider.append(ChatColor.YELLOW, "====Farms====");

            Reference.farmList.forEach((fname, farm) -> buider.newLine(ChatColor.WHITE)
                    .append(ChatColor.YELLOW, farm.getFancyName())
                    .append(ChatColor.YELLOW, " = ")
                    .append(ChatColor.WHITE, farm.getName())
                    .append(ChatColor.AQUA, "->")
                    .append(ChatColor.GOLD, Arrays.toString(farm.getSizes().keySet().toArray())));
            buider.send();
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
            return null;
        }
    }

    private class placeOption extends subOption {
        public placeOption(ABFMain abfMain) {
            super(abfMain, "place", "places the specified farm on you");
        }

        @Override
        public boolean execute(CommandSender sender, String label, ArrayList<String> args) {
            boolean success = false;
            ChatBuilder builder = new ChatBuilder(sender, ChatColor.WHITE);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (args.size() >= 3) {
                    String farm = args.get(args.size() - 2);
                    if (Reference.farmList.containsKey(farm.toLowerCase())) {
                        Farm f = Reference.farmList.get(farm.toLowerCase());
                        String size = args.get(args.size() - 1);
                        if (f.getSizes().containsKey(size)) {
                            Farm.Size s = f.getSizes().get(size);
                            builder.append(ChatColor.GREEN, "Placing farm:" + f.getFancyName()).append(ChatColor.GREEN, " size:" + s.getFancyName());
                            s.getSchem().pasteSchematic(p.getWorld(), p.getLocation());
                            success = true;
                        }
                    } else {
                        builder.append(ChatColor.RED, "farm:" + farm).append(ChatColor.RED, " not found");
                    }
                } else {
                    builder.append(ChatColor.RED, "Usage:/abf place <farm> <size>");
                }
            } else {
                builder.append(ChatColor.RED, "Command can only be executed by player").newLine(ChatColor.RED)
                        .append(ChatColor.RED, "You are:" + sender.getName()).append(ChatColor.RED, " " + sender.toString());
            }
            builder.send();
            return success;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
            ArrayList<String> strings = new ArrayList<>();
            String part = args[args.length - 1];
            if (args.length >= 3) {
                if (Reference.farmList.containsKey(args[2])) {
                    Farm f = Reference.farmList.get(args[2]);
                    f.getSizes().keySet().forEach(key -> {
                        if (key.startsWith(part)) {
                            strings.add(key);
                        }
                    });
                }
            } else {
                Reference.farmList.forEach((key, value) -> {
                    if (key.startsWith(part.toLowerCase())) {
                        strings.add(key);
                    }
                });
            }
            return strings;
        }
    }
}
