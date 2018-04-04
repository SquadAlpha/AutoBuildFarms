package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

abstract class subOption {
    protected final ABFMain parent;
    @Getter
    private final String name;
    @Getter
    private final String helpText;
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
