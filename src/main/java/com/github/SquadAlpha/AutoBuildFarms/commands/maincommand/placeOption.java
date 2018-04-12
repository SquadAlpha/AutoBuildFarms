package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import com.github.SquadAlpha.AutoBuildFarms.utils.Farm;
import com.github.SquadAlpha.AutoBuildFarms.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class placeOption extends subOption {
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
                        success = doPlace(f, size, builder, p);
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

    private boolean doPlace(Farm f, String size, ChatBuilder builder, Player p) {
        Farm.Size s = f.getSizes().get(size);
        if (Utils.perms.checkPlacePermsAndYell(p, f, s, builder, "place")) {
            if (Utils.econ.takeMoneyForFarmAndYell(p, f, s, builder, "place")) {
                builder.append(ChatColor.GREEN, "Placing farm:" + f.getFancyName()).append(ChatColor.GREEN, " size:" + s.getFancyName());
                s.getSchem().pasteSchematic(p.getWorld(), p.getLocation());
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        String part = args[args.length - 1];
        if (args.length >= 3) {
            if (Reference.farmList.containsKey(args[1])) {
                Farm f = Reference.farmList.get(args[1]);
                f.getSizes().keySet().forEach(key -> {
                    if (key.startsWith(part)) {
                        strings.add(key);
                    }
                });
            }
        } else if (args.length >= 2) {
            Reference.farmList.forEach((key, value) -> {
                if (key.startsWith(part.toLowerCase())) {
                    strings.add(key);
                }
            });
        }
        return strings;
    }
}
