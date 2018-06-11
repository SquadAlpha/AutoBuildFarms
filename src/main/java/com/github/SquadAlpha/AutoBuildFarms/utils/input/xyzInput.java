package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.xyz;
import me.lucko.helper.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class xyzInput extends PlayerInput<xyz> {

    public xyzInput(Player player, String requestText, String successText, String cancelText, AtomicBoolean canceled) {
        super(player, requestText, successText, cancelText, canceled);
    }


    @Override
    public void go() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> e.getPlayer().equals(this.getPlayer()))
                .biHandler((sub, event) -> {
                    if (event.getMessage().equals("cancel")) {
                        this.cancel();
                    }
                    String[] split = event.getMessage().split(" ");
                    try {
                        int x = Integer.parseInt(split[0]);
                        int y = Integer.parseInt(split[1]);
                        int z = Integer.parseInt(split[2]);
                        this.setAnswer(new xyz(x,y,z));
                        sub.unregister();
                    } catch (NumberFormatException e) {
                        this.getCancel().append(ChatColor.YELLOW,"Please type your location in the form \"x y z\"").send();
                    }
                        event.setCancelled(true);
                });
    }
}
