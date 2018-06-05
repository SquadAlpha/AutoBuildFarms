package com.github.SquadAlpha.AutoBuildFarms.commands;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class ABFCommand implements TabExecutor {
    @Getter
    private final PluginCommand command;

    @Getter
    private final AutoBuildFarms plugin;

    protected ABFCommand(String name, AutoBuildFarms plugin) {
        this.command = register(name, plugin);
        this.plugin = plugin;
    }

    private PluginCommand register(String name, AutoBuildFarms plugin) {
        List<String> aliases;
        Object tAliases = plugin.getDescription().getCommands().get(name).get("aliases");
        if (tAliases instanceof List) {
            aliases = (List<String>) tAliases;
        } else {
            aliases = new ArrayList<>();
            aliases.add(name);
        }
        String description = (String) plugin.getDescription().getCommands().get(name).get("description");
        String permission = (String) plugin.getDescription().getCommands().get(name).get("permission");
        String permissionMessage = (String) plugin.getDescription().getCommands().get(name).get("permission-message");
        String usage = (String) plugin.getDescription().getCommands().get(name).get("usage");
        plugin.registerCommand(this, aliases.toArray(new String[0]));
        for (String ali : aliases) {
            PluginCommand command = plugin.getCommand(ali);
            command.setName(name);
            command.setAliases(aliases);
            command.setDescription(description);
            command.setPermission(permission);
            command.setPermissionMessage(permissionMessage);
            command.setUsage(usage);
        }
        return plugin.getCommand(name);
    }


    @Override
    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    @Override
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

}
