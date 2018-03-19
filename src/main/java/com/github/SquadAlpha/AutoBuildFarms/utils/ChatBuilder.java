package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.reference.Reference;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatBuilder{
    private StringBuilder msg;
    private final CommandSender target;

    public ChatBuilder(CommandSender target,ChatColor c){
        this.target = target;
        this.msg = new StringBuilder(ChatColor.GREEN + "[" + Reference.plugin.getDescription().getPrefix() + "] " + c);
    }
    public ChatBuilder append(ChatColor c, String msg){
        this.msg.append(c)
                .append(msg);
        return this;
    }

    public ChatBuilder newLine(ChatColor c){
        this.msg.append("\r\n")
                .append(ChatColor.GREEN)
                .append("[")
                .append(Reference.plugin.getDescription().getPrefix())
                .append("] ")
                .append(c);
        return this;
    }


    public void send(){
        target.sendMessage(this.msg.toString());
    }
}
