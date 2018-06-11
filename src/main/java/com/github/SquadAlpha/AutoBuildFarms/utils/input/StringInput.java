package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import me.lucko.helper.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class StringInput extends PlayerInput<String> {


    public StringInput(Player player, String requestText, String successText, String cancelText, AtomicBoolean canceled) {
        super(player, requestText, successText, cancelText, canceled);
        this.getRequest().newLine().append(ChatColor.GRAY, "color code paragraphs replaced by &'s");
    }

    @Override
    public void go() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> e.getPlayer().equals(this.getPlayer()))
                .biHandler((sub, event) -> {
                    if (event.getMessage().equals("cancel")) {
                        this.cancel();
                    }
                    this.setAnswer(event.getMessage().replaceAll("(?<!\\\\)&", "§"));
                    sub.unregister();
                    event.setCancelled(true);
                });
    }
}
