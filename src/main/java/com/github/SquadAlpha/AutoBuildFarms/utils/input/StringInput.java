package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import me.lucko.helper.Events;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class StringInput implements PlayerInput<String> {


    private final ChatBuilder request;
    private final CountDownLatch cdl;
    private final AtomicBoolean canceled;
    private final Player player;
    private final ChatColor ansC;
    private final String ansS;

    private String answer;


    public StringInput(Player player, ChatColor requestColor, String requestString, ChatColor answerColor, String answerString, AtomicBoolean canceled) {
        this.player = player;
        this.request = new ChatBuilder(player).append(requestColor,requestString);
        this.ansC = answerColor;
        this.ansS = answerString;
        this.cdl = new CountDownLatch(1);
        this.canceled = canceled;
    }

    @Override
    public String await() {
        try {
            this.cdl.await();
            new ChatBuilder(player)
                    .append(this.ansC,this.ansS.replace("%1",this.answer));
        } catch (InterruptedException e) {
            this.canceled.set(true);
            e.printStackTrace();
        }
        return this.answer;
    }

    @Override
    public void go() {
        this.request.newLine().append(ChatColor.GRAY, "color code paragraphs replaced by &'s").send();
        Events.subscribe(AsyncPlayerChatEvent.class)
                .filter(e -> e.getPlayer().equals(player))
                .biHandler((sub, event) -> {
                    if (event.getMessage().equals("cancel")) {
                        canceled.set(true);
                        cdl.countDown();
                    }
                    this.answer = event.getMessage().replaceAll("(?<!\\\\)&", "§");
                    sub.unregister();
                    cdl.countDown();
                    event.setCancelled(true);
                });
    }
}
