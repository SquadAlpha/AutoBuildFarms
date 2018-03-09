package com.github.SquadAlpha.AutoBuildFarms.utils;

import com.github.SquadAlpha.AutoBuildFarms.Reference;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatBuilder{
    private StringBuilder msg;
    private final CommandSender target;

    public ChatBuilder(CommandSender target,ChatColor c){
        this.target = target;
        this.msg = new StringBuilder(ChatColor.COLOR_CHAR + c.getChar() + "["+ Reference.plugin.getDescription().getPrefix()+"]");
    }
    public ChatBuilder append(ChatColor c, String msg){
        this.msg.append(ChatColor.COLOR_CHAR)
                .append(c.getChar())
                .append(msg);
        return this;
    }

    public ChatBuilder newLine(ChatColor c){
        this.msg.append("\r\n")
                .append(ChatColor.COLOR_CHAR)
                .append(c.getChar())
                .append("[")
                .append(Reference.plugin.getDescription().getPrefix())
                .append("]");
        return this;
    }

    public void send(){
        target.sendMessage(this.msg.toString());
    }
}
