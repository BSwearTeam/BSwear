package io.github.bswearteam.bswear;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.permissions.Permission;
import io.github.bswearteam.bswear.Main;

public class CommandSwear implements Listener {

    private Main main;

    public CommandSwear(Main m) {
		main = m;
	}

   public final Permission CmdSwearBypass = new Permission("bswear.bypass.commandbypass");
   
   
   @EventHandler
   public void onCommandSwear(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      String cmd = event.getMessage();
      event.getMessage().substring(1).toLowerCase();
      if (cmd.startsWith("/broadcast") || cmd.startsWith("/me")) {
    	  if (!player.hasPermission(CmdSwearBypass) || !player.hasPermission(Main.BypassPerm)) {
    		  String msg = event.getMessage().toLowerCase().replaceAll("[-_@]", "");
    		  String sc = main.getConfig().getString("command");
    		  String swearmsg = ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD + "We've detected a swear word MIGHT be in your message so we blocked that word!";
    		  String swearer = player.getName();
    		  for (String word : main.getSwearConfig().getStringList("words")) {
    			  if (msg.contains(word) && !msg.toLowerCase().contains("hello")) {
  					
    				  if (main.getConfig().getBoolean("cancelMessage") == true) {
    					  event.setCancelled(true);
  						} else {
  							String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
  							event.setMessage(messagewithoutswear);
  							player.sendMessage(swearmsg);
  						}
  				
  						// The flowing Will check the config, to see if the user has it enabled :)
  						SwearUtils.runCommand(sc, swearer);
  						SwearUtils.sendTitle(player);
  						SwearUtils.kickSwearer(player);
  						SwearUtils.banSwearer(player);
  					}
  				}
    	  }
      }
   }
}
