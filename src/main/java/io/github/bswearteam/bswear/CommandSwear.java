package io.github.bswearteam.bswear;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
      String command = event.getMessage().substring(1).toLowerCase();
      if (command.startsWith("broadcast") || command.startsWith("me") || command.startsWith("tell") || command.startsWith("msg") || command.startsWith("pm")) {
    	  if (!player.hasPermission(CmdSwearBypass) || !player.hasPermission(Main.BypassPerm)) {
    		  command = command.replaceAll("[-_@]", "");
    		  String sc = main.getConfig().getString("command");
    		  String swearmsg = ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD + "We've detected a swear word that MIGHT be in your message so we blocked that word!";
    		  for (String word : main.getSwearConfig().getStringList("words")) {
    			  if (command.contains(word) && !command.toLowerCase().contains("hello")) {
  					
    				  if (main.getConfig().getBoolean("cancelMessage") == true) {
    					  event.setCancelled(true);
  						} else {
  							String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
  							event.setMessage(messagewithoutswear);
  							player.sendMessage(swearmsg);
  						}
  				
  						// The flowing Will check the config, to see if the user has it enabled :)
    				  SwearUtils.checkAll(sc, player);
  					}
  				}
    	  }
      }
   }
}
