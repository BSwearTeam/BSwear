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
	
	
    String pwar = "2";
    String maxwar = "2";    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String JoinPrefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "BSwear" + ChatColor.BLUE + "]" + " ";
        JoinPrefix = main.prefix;
        player.sendMessage(JoinPrefix + ChatColor.GRAY + "Our Antiswearing filter is protecting this server!");
        //TODO player.sendMessage(JoinPrefix + ChatColor.GRAY + "You have " + pwar + "/" + maxwar + " Warnings left");
    }
}
