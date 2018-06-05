package com.github.SquadAlpha.AutoBuildFarms.utils;

import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Data
public class onCommandArgs {
    private final CommandSender sender;
    private final Command cmd;
    private final String label;
    private final String[] args;
}
