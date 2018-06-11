package com.github.SquadAlpha.AutoBuildFarms.utils.input;

import com.github.SquadAlpha.AutoBuildFarms.utils.ChatBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PlayerInput<T> {

    private final CountDownLatch cdl;
    private final AtomicBoolean canceled;

    private T answer;

    private final String successText;
    @Getter(AccessLevel.PROTECTED) private final Player player;
    @Getter(AccessLevel.PROTECTED) private final ChatBuilder request;
    @Getter(AccessLevel.PROTECTED) private final ChatBuilder cancel;


    protected PlayerInput(Player player, String requestText, String successText, String cancelText, AtomicBoolean canceled) {
        this.cdl = new CountDownLatch(1);
        this.player = player;
        this.request = new ChatBuilder(this.player).append(ChatColor.AQUA,requestText);
        this.successText = successText;
        this.cancel = new ChatBuilder(this.player).append(ChatColor.RED, cancelText);
        this.canceled = canceled;
    }

    protected final void setAnswer(T ans){
        this.answer = ans;
        this.getSuccess().send();
        this.cdl.countDown();
    }

    protected final void cancel(){
        this.canceled.set(true);
        this.cancel.send();
        this.cdl.countDown();
    }

    //Wait for the selection to be completed
    public final T await(){
        try {
            this.cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace(); }
        return this.answer;
    }


    public final void start(){
        this.request.send();
        this.go();
    }
    //Start the selection process please only hook events here
    protected abstract void go();

    protected final ChatBuilder getSuccess(){
        return new ChatBuilder(this.player).append(ChatColor.GREEN, successText.replace("%1",this.answer.toString()));
    }
}
