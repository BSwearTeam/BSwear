package io.github.bswearteam.bswear;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandSwear implements Listener {
    private BSwear main;
    public CommandSwear(BSwear m) {
		main = m;
	}

    @EventHandler
    public void onCommandSwear(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().substring(1).toLowerCase();
        if (ifStartsWith(command, "broadcast", "me", "tell", "msg", "pm", "whisper", "reply", "say", "tellraw", "title")) {
            if (!player.hasPermission("bswear.bypasscommands") || !player.hasPermission(BSwear.BypassPerm)) {
                command = command.replaceAll("[-_@]", "");
                for (String word : main.getSwearConfig().getStringList("warnList")) {
                    boolean a = false;

                    String[] messageAsArray = command.split(" ");

                    int messageLength = messageAsArray.length;
                    for (int i = 0; i < messageLength;) {
                        String partOfMessage = messageAsArray[i];
                        StringBuilder strBuilder = new StringBuilder();
                        char[] messageAsCharArray = partOfMessage.toLowerCase().toCharArray();
                        for (int h = 0; h < messageAsCharArray.length;) {
                            char character = messageAsCharArray[h];
                            if (character >= '0' && character <= '9' || character >= 'a' && character <= 'z') {
                                strBuilder.append(character);
                            }
                            h++;
                        }

                        if (strBuilder.toString().equalsIgnoreCase(word.toLowerCase())) a = true;
   
                        i++;
                    }

                    if (a) {
                        if (main.getConfig().getBoolean("cancelMessage") == true) {
                            event.setCancelled(true); // Cancel Message
                        } else {
                            String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
                            event.setMessage(messagewithoutswear);
                            event.getPlayer().sendMessage(ChatColor.DARK_GREEN+"[BSwear] "+ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD +"We've detected a swear word MIGHT be in your message so we blocked that word!");
                        }
                      
                        // The flowing Will check the config, to see if the user has it enabled :)
                        SwearUtils.checkAll(main.getConfig().getString("command"), event.getPlayer());
                    }
                }
			}
		}
	}
    
    public boolean ifStartsWith(String a, String...strs) {
        boolean b = false;
        for (String s : strs) {
            if (a.startsWith(s)) {
                b = true;
            }
        }
        return b;
    }
}
