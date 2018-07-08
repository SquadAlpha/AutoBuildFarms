package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.AutoBuildFarms;
import com.github.SquadAlpha.AutoBuildFarms.commands.CommandWithSubCommands;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ABFAdmin extends CommandWithSubCommands {

    public ABFAdmin(AutoBuildFarms plugin) {
        super("ABFadmin", plugin);
        this.registerSubOption(new SubOption(this, "createtype", CreateType.getFunc(), CreateType.getTabComplete()));
        this.registerSubOption(new SubOption(this, "createsize", CreateSize.getFunc(), CreateSize.getTabComplete()));
    }

    @Override
    protected boolean mainCommand(CommandSender sender, Command command, String label, String[] args) {
        //TODO open gui
        return false;
    }

}
