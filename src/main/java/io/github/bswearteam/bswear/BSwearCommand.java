package io.github.bswearteam.bswear;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BSwearCommand implements CommandExecutor {

    public BSwear m;
    public BSwearCommand(BSwear b){this.m = b;}

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bswear")) {
            if (sender.hasPermission("bswear.command.use") || sender.isOp() || sender.hasPermission("bswear.*")) {
                if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("version"))) {
                    sendMessage(sender, m.prefix + ChatColor.AQUA + "BSwear v" + m.version,
                            ChatColor.AQUA + "Cmd Help: /bswear help");
                } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    sendMessage(sender, m.prefix + " Usage: /bswear <option>", m.prefix + " Options:");
                    showCommandUsage(sender);
                } else if (args.length == 2) {
                    if (args[0].equalsIgnoreCase("add")) addOrRemoveWord(true, args[1], sender);
                    if (args[0].equalsIgnoreCase("remove")) addOrRemoveWord(false, args[1], sender);

                } else if (args.length == 1 && args[0].equalsIgnoreCase("wordlist")) {
                    sendMessage(sender, "Blocked words: " + m.swears.getStringList("warnList")
                                .toString().replace("[", "").replace("]", ""));

                } else if (args.length == 1 && args[0].equalsIgnoreCase("swearers")) {
                    Set<String> keys = SwearUtils.swearers.keySet();
                    String swearers = "";
                    for (String uuid : keys)
                        swearers += Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + ", ";

                    sender.sendMessage(ChatColor.BLUE + "Swearers:");
                    sender.sendMessage(swearers);

                } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    m.swears.set("warnList", Collections.emptyList());
                    m.saveConf(m.swears, m.swearf);

                    sendMessage(sender, m.prefix + "All blocked words have been unblocked!");

                } else if (args.length == 2 && args[0].equalsIgnoreCase("prefix")) {
                    m.getConfig().set("messages.prefix", args[1]);
                    m.saveConfig();
                    m.prefix = ChatColor.translateAlternateColorCodes('&', args[1]+" ");
                    sender.sendMessage("Prefix changed to: " + m.prefix);

                } else if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("useTitles")) {
                        if (args.length == 2) {
                            boolean t = args[1].equalsIgnoreCase("true") || args[1].startsWith("y") || args[1].equalsIgnoreCase("enable");
                            m.getConfig().set("sendTitle", t);
                            m.saveConfig();
                            sendMessage(sender, m.prefix + "Title on swear is now " + t);
                        } else sendMessage(sender, "TitleAPI enabled: " + m.getConfig().getBoolean("sendTitle"));
                    } else if (args[0].equalsIgnoreCase("allowViewPlayer")) {
                        if (args.length == 1) {
                            sender.sendMessage(ChatColor.RED + "Usage: /bswear allowViewPlayer <Player>");
                            return true;
                        }
                        List<String> plrs = m.getConfig().getStringList("allowViewPlayers");
                        String plr = args[1].toLowerCase();
                        boolean add = !plrs.contains(plr);
                        if (add) plrs.add(plr); else plrs.remove(plr);
                        m.getConfig().set("allowViewPlayers", plrs);
                        m.saveConfig();
                        sender.sendMessage(m.prefix + " Player " + args[1] + " can now " + (add ? "" : "NOT") +
                                "read swear messages.");
                    }
                } else sender.sendMessage(m.prefix + "Error! Wrong args, do \"/bswear help\" for command help");
            } else sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("messages.noperm")));

            return false;
        }
        return false;
    }

    public void addOrRemoveWord(boolean add, String arg, CommandSender sender) {
        List<String> words = m.swears.getStringList("warnList");
        String word = arg.toLowerCase();
        if ((add && !words.contains(word)) || (!add && words.contains(word))) {
            if (add) words.add(word); else words.remove(word);

            m.swears.set("warnList", words);
            m.saveConf(m.swears, m.swearf);
            sendMessage(sender, m.prefix + m.getConfig().getString("messages." + (add ? "addword" : "delword")));
        } else sendMessage(sender, m.prefix + ChatColor.RED + "Error! This word is " + (add ? "already" : "not") + " blocked!");
    }

    private void showCommandUsage(CommandSender s) {
        String[] options = {
                "<add/remove> <word> - Blocks/Unblocks a word",
                "clear - Unblock all words",
                "wordlist - Show all blocked words",
                "prefix <text> - Sets the prefix",
                "swearers - Show all swearers",
                "useTitles - set using title on swear",
                "allowViewPlayer <player> - allow per player antiswear for player"
                };
        for (String option : options) {
            String[] strs = option.split("-");
            sendMessage(s, ChatColor.GOLD + "/bswear " + strs[0] + ChatColor.GREEN + " - " + strs[1]);
        }
    }
    
    public void sendMessage(CommandSender s, String... ms) {
        for (String msg : ms)if (s instanceof Player) s.sendMessage(msg); else s.sendMessage(ChatColor.stripColor(msg));
    }

}