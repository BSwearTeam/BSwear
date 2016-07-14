package io.github.bswearteam.bswear;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import io.github.bswearteam.bswear.Main;

public class OnJoin implements Listener {

    //final FileConfiguration config = Bukkit.getPluginManager().getPlugin("BSwear").getConfig();

    String pwar = "2";
    String maxwar = "2";    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String JoinPrefix = ChatColor.BLUE + "[" + ChatColor.AQUA + "BSwear" + ChatColor.BLUE + "]" + " ";
        player.sendMessage(JoinPrefix + ChatColor.GRAY + "Our Antiswearing filter is protecting this server!");
        player.sendMessage(JoinPrefix + ChatColor.GRAY + "You have " + pwar + "/" + maxwar + " Warnings left");
    }
}