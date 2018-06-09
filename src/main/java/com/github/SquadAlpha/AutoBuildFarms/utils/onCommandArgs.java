package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

@Data
public class onCommandArgs {
    private final CommandSender sender;
    private final PluginCommand cmd;
    private final String label;
    private final String[] args;
}
