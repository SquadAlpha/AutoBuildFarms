package com.github.SquadAlpha.AutoBuildFarms.commands.maincommand.menu;

import com.github.SquadAlpha.AutoBuildFarms.config.Config;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

public class MainMenu extends Gui {

    /* TODO calculate lines  */

    public MainMenu(Player player, int lines) {
        super(player, lines, Config.getMainMenuTitle());
    }


    @Override
    public void redraw() {
        // TODO make this and the firstdraw
    }
}
