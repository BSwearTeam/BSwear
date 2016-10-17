package io.github.bswearteam.bswear;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SwearUtils {
	
	private static Main main; // Hook in to main class
	
	
	public SwearUtils(Main m) {
		main = m;
	}
	
	public SwearUtils(){
	}
	
	/**
	 * if commandenable is true,
	 * then an {@link Command} is run.
	 * 
	 * @param sc <i>the command to run</i>
	 * @param swearer <i>the player that is swearing, in an String format</i>
	 * */
	public static void runCommand(String sc, String swearer) {
		if (!main.getConfig().getBoolean("commandenable", false)) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sc.replace("%swearer%", swearer));
		}
	}
	
	
	/**
	 * if commandenable is true,
	 * then an {@link Command} is run.
	 * 
	 * @param sc <i>the command to run</i>
	 * @param swearer <i>the player that is swearing, in an {@link Player} format</i>
	 * */
	public static void runCommand(String sc, Player swearer) {
		runCommand(sc, swearer.getName());
	}
	
	
	/**
	 * if sendTitle is true,
	 * the swearer will get an message as an "title" in the middle of there screen, saying No Swearing
	 * */
	public static void sendTitle(Player player) {
		if (main.getConfig().getBoolean("sendTitle") == true) {
			TitlesAPI.sendTitle(player, "ERROR", "No Swearing", true);
		}
	}
	
	/**
	 * if kickSwearer is true, 
	 * this will kick them
	 * */
	public static void kickSwearer(Player player) {
		if (main.getConfig().getBoolean("banSwearer") == false && main.getConfig().getBoolean("kickSwearer") == true) {
			player.kickPlayer("We've detected a swear word MIGHT be in your message so we kicked you ");
		}
	}
	
	/**
	 * if banSwearer is true, 
	 * this will ban them
	 * */
	@SuppressWarnings("deprecation")
	public static void banSwearer(Player player) {
		if (main.getConfig().getBoolean("banSwearer") == true && main.getConfig().getBoolean("kickSwearer") == false) {
			player.kickPlayer("We've detected a swear word in your msg, so its setup to ban you");
			player.setBanned(true); // Is there a better way to ban the player?
		}
	}
	
	/**
	 * Checks every thing!
	 * */
	public static void checkAll(String sc, Player player) {
		SwearUtils.runCommand(sc, player);
		SwearUtils.sendTitle(player);
		SwearUtils.kickSwearer(player);
		SwearUtils.banSwearer(player);
	}
}
