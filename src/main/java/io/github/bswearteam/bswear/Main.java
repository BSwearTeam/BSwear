package io.github.bswearteam.bswear;

 import java.util.logging.Logger;
 import org.bukkit.Bukkit;
 import org.bukkit.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.Server;
 import org.bukkit.Sound;
 import org.bukkit.World;
 import org.bukkit.configuration.file.FileConfiguration;
 import org.bukkit.configuration.file.FileConfigurationOptions;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.AsyncPlayerChatEvent;
 import org.bukkit.permissions.Permission;
 import org.bukkit.plugin.PluginManager;
 import org.bukkit.plugin.java.JavaPlugin;
 import org.bukkit.util.*;
 
 import org.apache.commons.lang.StringUtils;
 
 import io.github.bswearteam.bswear.TitlesAPI;
 
 public class Main extends JavaPlugin implements Listener {
    public Permission BypassPerm = new Permission("bswear.bypass");
    public Permission allPerm = new Permission("bswear.*");
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.addPermission(BypassPerm);
        getConfig().options().copyDefaults(true);
        saveConfig();
        pm.registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "BSwear protects this server!");
        getLogger().info(ChatColor.GREEN + "BSwear uses AdityaTD's Cluster API");
    }
    
    //Checks what words the players are saying
    @EventHandler
    public void onSwear(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        for (String word : getConfig().getStringList("warnList")) {
            String msg = e.getMessage().toLowerCase();
            if ((e.getMessage().toLowerCase().contains(word)) && (!player.hasPermission("BSwear.ignore")) && (msg.contains(word))) {
                //What happens when a word on the Warn List is executed
                if (getConfig().getBoolean("cancelMessage") == true) {
                    e.setCancelled(true);
                } else {
                    String messagewithoutswear = e.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
                    e.setMessage(messagewithoutswear);
                }
                e.getPlayer().sendMessage(ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD + "We've detected a swear word MIGHT be in your message so we blocked that word!");
                if (getConfig().getBoolean("sendTitle") == true) {
                    TitlesAPI.sendFullTitle(e.getPlayer(), 10, 80, 10, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "Don't Swear!");
                }
            }
        }
    }
    public void onDisable(){}
}
