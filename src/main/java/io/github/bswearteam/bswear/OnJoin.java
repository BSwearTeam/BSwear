package io.github.bswearteam.bswear;
 
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    private BSwear main;

	public OnJoin(BSwear m) {
		main = m;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (main.getConfig().getBoolean("showJoinMessage") == true) {
            int plrWarnings = 0;
            
            if (!SwearUtils.hasSweared(e.getPlayer())) {
                plrWarnings = SwearUtils.getPlrSwears(e.getPlayer());
            }
            
            e.getPlayer().sendMessage(main.prefix + ChatColor.GRAY + "Our Antiswearing filter is protecting this server!");
            e.getPlayer().sendMessage(main.prefix + ChatColor.GRAY + "You have "+plrWarnings+" warnings out of max "+main.getConfig().getDouble("maxWarnings"));
        }
    }
}