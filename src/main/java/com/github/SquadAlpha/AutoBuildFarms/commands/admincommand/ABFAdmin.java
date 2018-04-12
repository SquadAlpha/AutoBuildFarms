package com.github.SquadAlpha.AutoBuildFarms.commands.admincommand;

import com.github.SquadAlpha.AutoBuildFarms.commands.ABFWithSubCommand;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import org.bukkit.command.CommandSender;

public class ABFAdmin extends ABFWithSubCommand {
    protected ABFAdmin() {
        super("ABFadmin", "the administration command for " + Reference.plugin.getName(), "/abfadmin",
                "abfadmin", "abfa", "autobuildfarmsadmin");
        //TODO register subOptions

    }

    @Override
    protected boolean onMainExecute(CommandSender sender, String commandLabel, String[] args) {
        return this._execute(sender, commandLabel, new String[]{"INVALIDSUBOPTIONTOTRIGGERHELP"});
    }
}
