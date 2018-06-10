package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import me.lucko.helper.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class DoubleInput implements PlayerInput<Double>{
    private final ChatBuilder request;
    private final CountDownLatch cdl;
    private final AtomicBoolean canceled;
    private final Player player;
    private final ChatColor ansC;
    private final String ansS;

    private Double answer;

    public DoubleInput(Player player, ChatColor requestColor, String requestString, ChatColor answerColor, String answerString, AtomicBoolean canceled) {
        this.player = player;
        this.request = new ChatBuilder(player).append(requestColor,requestString);
        this.ansC = answerColor;
        this.ansS = answerString;
        this.cdl = new CountDownLatch(1);
        this.canceled = canceled;
    }

    @Override
    public Double await() {
        try {
            this.cdl.await();
            new ChatBuilder(player)
                    .append(this.ansC,this.ansS.replace("%1",this.answer.toString()));
        } catch (InterruptedException e) {
            this.canceled.set(true);
            e.printStackTrace();
        }

        return this.answer;
    }

    @Override
    public void go() {
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> e.getPlayer().equals(player))
                .biHandler((sub, event) -> {
                    if (event.getMessage().equals("cancel")) {
                        canceled.set(true);
                        cdl.countDown();
                    }
                    try {
                        this.answer = Double.parseDouble(event.getMessage());
                    } catch (NumberFormatException e) {
                        new ChatBuilder(event.getPlayer())
                                .append(ChatColor.RED,"Invalid double please the 10.99 notation").send();
                        return;
                    }
                    sub.unregister();
                    cdl.countDown();
                    event.setCancelled(true);
                });
    }
}
