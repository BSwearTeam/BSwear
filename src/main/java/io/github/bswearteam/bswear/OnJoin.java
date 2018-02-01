package io.github.bswearteam.bswear;
 
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {
    private BSwear m;
    public OnJoin(BSwear main){this.m = main;}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (m.getConfig().getBoolean("showJoinMessage")) {
            int pWarn = SwearUtils.getPlrSwears(e.getPlayer());
            //e.getPlayer().sendMessage(m.prefix +ChatColor.GRAY+ "Protected by powerful antiswear.");
            if (pWarn > 0) {
                e.getPlayer().sendMessage(m.prefix +ChatColor.GRAY+ "You have " + pWarn + " warnings out of max "+m.getConfig().getInt("maxWarnings"));
            }
        }
    }
}
