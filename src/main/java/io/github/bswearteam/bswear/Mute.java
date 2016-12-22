package io.github.bswearteam.bswear;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @Author BSwearTeam
 */
public class Mute implements Listener, CommandExecutor {
    
	private BSwear main;
    public Mute(BSwear m) {
		main = m;
	}

    @EventHandler
    public void onChatMute(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (main.getMutedConfig().getBoolean("muted."+player.getName().toLowerCase())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("[BSwearMuteManger] " + "You are muted");
        }
    }
    @SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

            if (sender.hasPermission("bswear.command.mute") || sender.isOp()) {
                if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
                    Player mutePlayer = Bukkit.getPlayer(args[1]);
                    if (mutePlayer == null) {
                        sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED  + " is not online");
                        return true;
                    } else {
                        main.getMutedConfig().set("muted."+mutePlayer.getName().toLowerCase(), true);
                        sender.sendMessage(ChatColor.RED + "Player " + mutePlayer.getName() + ChatColor.RED + " muted!");
                        return true;
                    }
                } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                    Player mutePlayer = Bukkit.getPlayer(args[1]);
                    if (mutePlayer == null) {
                        sender.sendMessage(ChatColor.YELLOW + args[1] + ChatColor.RED  + " is not online");
                        return true;
                    } else {
                        main.getMutedConfig().set("muted."+mutePlayer.getName().toLowerCase(), null);
                        sender.sendMessage("[BSwearMuteManger] " + ChatColor.RED + "Player " + mutePlayer.getName() + ChatColor.RED + " unmuted!");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Try /mute <add/remove> <player>");
                    return true;
                }
            } else {
            sender.sendMessage("No permission");
            }
        return false;
    }
}
