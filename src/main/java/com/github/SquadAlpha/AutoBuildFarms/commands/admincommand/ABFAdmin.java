package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandwithSubcommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ABFAdmin extends CommandwithSubcommands {

    public ABFAdmin(AutoBuildFarms plugin) {
        super("ABFadmin", plugin);
        this.registerSubOption(new subOption(this, "createtype", CreateType.func));
        this.registerSubOption(new subOption(this,"createsize",CreateSize.func));
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO open gui
        return false;
    }

}
