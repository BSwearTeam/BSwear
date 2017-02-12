package io.github.bswearteam.bswear;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Advertising implements Listener {
    private BSwear m;
    public Advertising(BSwear b) { m = b;}

    @EventHandler
    public void OnChatAdvertising(AsyncPlayerChatEvent e) {
      Player p = e.getPlayer();
      if (!p.hasPermission(m.AdvertisingBypass)) {
         String msg = e.getMessage().toLowerCase().replaceAll("[-_*. ]", "");
         for (String ad : m.getConfig().getStringList("advertising")) {
            if (m.ifHasWord(msg, ad)) {
                if (m.getConfig().getBoolean("cancelMessage") == true) e.setCancelled(true);
                else {
                    String messagewithoutswear = e.getMessage().replaceAll(ad, StringUtils.repeat("*", ad.length()));
                    e.setMessage(messagewithoutswear);
                }
                
                e.getPlayer().sendMessage(m.prefix + ChatColor.RED + ChatColor.BOLD + "No advertising!");
                // The flowing Will check the config, to see if the user has it enabled :)
                SwearUtils.checkAll(m.getConfig().getString("command"), e.getPlayer());
            }
         }
      }
   }
}
