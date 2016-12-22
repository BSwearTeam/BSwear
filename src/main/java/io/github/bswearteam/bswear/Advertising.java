package io.github.bswearteam.bswear;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;

/**
 * @Author TheBSwearTeam
 */
public class Advertising implements Listener {

    private BSwear main;

    public Advertising(BSwear m) {
		main = m;
	}

   public final Permission ADVERTISING_PERM = new Permission("bswear.advertising.bypass");
   
   @EventHandler
   public void OnChatAdvertising(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPermission(ADVERTISING_PERM)) {
         String msg = event.getMessage().toLowerCase().replaceAll("[-_*. ]", "");
         for (String advert : main.getConfig().getStringList("advertising")) {
            if (msg.contains(advert)) {
                player.sendMessage("No Advertising");
            }
         }
      }
   }
}
