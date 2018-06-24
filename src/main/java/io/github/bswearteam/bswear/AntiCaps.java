package io.github.bswearteam.bswear;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiCaps implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        String noColor = ChatColor.stripColor(e.getMessage());
        if (noColor.equalsIgnoreCase(noColor.toUpperCase())) e.setMessage(e.getMessage().toLowerCase());
    }
}