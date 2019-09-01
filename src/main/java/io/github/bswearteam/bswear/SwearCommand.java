package io.github.bswearteam.bswear;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SwearCommand implements CommandExecutor {

    public BSwear m;
    public SwearCommand(BSwear b){this.m = b;}

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("swear")) {
            List<String> words = m.getConfig().getStringList("allowViewPlayers");
            String word = sender.getName().toLowerCase();
            if (!words.contains(word)) {
                words.add(word);
                m.getConfig().set("allowViewPlayers", words);
                m.saveConfig();
                sender.sendMessage(m.prefix + "You can now send and read swear messages.");
            } else {
                words.remove(word);
                m.getConfig().set("allowViewPlayers", words);
                m.saveConfig();
                sender.sendMessage(m.prefix + "You can now NOT send and read swear messages.");
            }
            return true;
        }
        return false;
    }

    public void sendMessage(CommandSender s, String msg) {
        if (s instanceof Player) s.sendMessage(msg);
        else Bukkit.getConsoleSender().sendMessage(ChatColor.stripColor(msg));
    }

}