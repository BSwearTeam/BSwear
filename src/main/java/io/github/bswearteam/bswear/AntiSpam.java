package io.github.bswearteam.bswear;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AntiSpam implements Listener {
    public HashMap<String, String> chat = new HashMap<>();
    public ArrayList<String> cooldown = new ArrayList<>();

    private BSwear b;
    public AntiSpam(BSwear b) {
        this.b = b;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (!b.getConfig().getBoolean("antispam.enable", true)) return;

        int time = b.getConfig().getInt("antispam.spamtimer", 3);
        Player p = e.getPlayer();
        String msg = ChatColor.stripColor(e.getMessage());

        if (chat.containsKey(p.getName())) {
            String s = chat.getOrDefault(p.getName(), "bswear123456789-987654321");
            if (!s.equalsIgnoreCase("bswear123456789-987654321") && s.equalsIgnoreCase(msg)) {
                p.sendMessage(ChatColor.RED + "Please wait " + (time + 3) + " seconds between similar chat messages.");
                e.setCancelled(true);
                return;
            }
        }
        
        if (cooldown.contains(p.getName())) {
            e.setCancelled(true);
            p.sendMessage(ChatColor.RED + "Please wait 1 second between chat messages.");
            return;
        }

        chat.put(p.getName(), msg);
        cooldown.add(p.getName());

        Bukkit.getScheduler().runTaskLater(b, () -> cooldown.remove(p.getName()), 20); // 20 ticks per second
        Bukkit.getScheduler().runTaskLater(b, () -> chat.remove(p.getName(), msg), (time + 3) * 20); // 20 ticks per second
    }
}