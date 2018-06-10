package com.github.SquadAlpha.AutoBuildFarms.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;

public class ChatBuilder {
    private final CommandSender sender;
    private final ComponentBuilder builder;

    public ChatBuilder(CommandSender sender) {
        this.sender = sender;
        this.builder = new ComponentBuilder("");
    }

    public ChatBuilder append(ChatColor color, String text) {
        TextComponent tc = new TextComponent(text);
        tc.setColor(color);
        this.builder.append(tc);
        return this;
    }

    public ChatBuilder newLine() {
        this.builder.append("\n");
        return this;
    }

    public void send() {
        this.sender.spigot().sendMessage(this.builder.create());
    }
}
