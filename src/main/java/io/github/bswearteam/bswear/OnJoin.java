package io.github.bswearteam.bswear;
 
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class OnJoin implements Listener {

    private Main main;

	public OnJoin(Main m) {
		main = m;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String JoinPrefix = main.prefix;
        player.sendMessage(JoinPrefix + ChatColor.GRAY + "Our Antiswearing filter is protecting this server!");
    }
}