package io.github.bswearteam.bswear;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.EventPriority;

public class Advertising implements Listener {
    private BSwear m;
    public Advertising(BSwear b) { m = b;}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void OnChatAdvertising(AsyncPlayerChatEvent e) {
      Player p = e.getPlayer();
      if (!p.hasPermission(m.AdvertisingBypass)) {
         String msg = e.getMessage().toLowerCase().replaceAll("[-_*. ]", "");
         m.getConfig().getStringList("advertising").stream().forEach((ad) -> {
            if (m.ifHasWord(msg, ad)) {
                if (m.getConfig().getBoolean("cancelMessage")) e.setCancelled(true);
                else {
                    String messagewithoutswear = e.getMessage().replaceAll(ad, SwearUtils.repeat("*", ad.length()));
                    e.setMessage(messagewithoutswear);
                }
                
                e.getPlayer().sendMessage(m.prefix + ChatColor.RED + ChatColor.BOLD + "No advertising!");
                SwearUtils.checkAll(m.getConfig().getString("command"), e.getPlayer());
            }
         });
      }
   }
}
