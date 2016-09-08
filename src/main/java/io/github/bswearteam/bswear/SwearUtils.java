package io.github.bswearteam.bswear;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SwearUtils {
	
	private static Main main; // Hook in to main class
	
	
	public SwearUtils() {
	}
	
	public static void runCommand(String sc, String swearer) {
		if (!main.getConfig().getBoolean("commandenable", false)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sc.replace("%swearer%", swearer));
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static void sendTitle(Player player) {
		if (main.getConfig().getBoolean("sendTitle") == true) {
			TitlesAPI.sendFullTitle(player, 10, 80, 10, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "No Swearing");
		}
	}
	
	public static void kickSwearer(Player player) {
		if (main.getConfig().getBoolean("banSwearer") == true && main.getConfig().getBoolean("kickSwearer") == false) {
			player.kickPlayer("We've detected a swear word MIGHT be in your message so we kicked you ");
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void banSwearer(Player player) {
		if (main.getConfig().getBoolean("banSwearer") == true && main.getConfig().getBoolean("kickSwearer") == false) {
			player.kickPlayer("We've detected a swear word in your msg, so its setup to ban you");
			player.setBanned(true); // Is there a better way to ban the player?
		}
	}
}
