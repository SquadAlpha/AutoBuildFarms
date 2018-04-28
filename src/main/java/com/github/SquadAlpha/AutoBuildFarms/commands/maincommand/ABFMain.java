package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand;

import com.github.SquadAlpha.AutoBuildFarms.commands.ABFWithSubCommand;
import com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu.MainMenu;
import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ABFMain extends ABFWithSubCommand {


    public ABFMain() {
        super("ABF", "the main " + Reference.plugin.getName() + " command", "/ABF help", "abf", "autobuildfarms");
        this.addSubOption(new helpOption(this));
        this.addSubOption(new listOption(this));
        this.addSubOption(new placeOption(this));
    }


    private void openMenu(Player sender) {
        MainMenu menu = new MainMenu(sender);
        menu.open();
    }


    @Override
    protected boolean onMainExecute(CommandSender sender, String commandLabel, String[] args) {
        if (this.isPlayerAndYell(sender)) {
            Player p = (Player) sender;
            openMenu(p);
            return true;
        } else {
            return false;
        }
    }
}
