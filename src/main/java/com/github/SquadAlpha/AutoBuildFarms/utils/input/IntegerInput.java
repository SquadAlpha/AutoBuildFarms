package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import me.lucko.helper.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class IntegerInput extends PlayerInput<Integer> {

    public IntegerInput(Player player, String requestText, String successText, String cancelText, AtomicBoolean canceled) {
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
                    try {
                        this.setAnswer(Integer.parseInt(event.getMessage()));
                        sub.unregister();
                    } catch (NumberFormatException e) {
                        new ChatBuilder(event.getPlayer())
                                .append(ChatColor.RED,"Invalid integer please enter an unformatted whole number").send();
                    }
                    event.setCancelled(true);
                });
    }
}
