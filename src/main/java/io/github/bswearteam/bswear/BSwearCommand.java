package io.github.bswearteam.bswear;

import java.util.List;
import java.util.Set;

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
            if (sender.hasPermission(m.CommandPerm) || sender.isOp() || sender.hasPermission(m.allPerm)) {
                if (args.length == 0) {
                    sendMessage(sender, m.prefix);
                    sendMessage(sender, ChatColor.AQUA + "BSwear v" + m.version);
                    sendMessage(sender, ChatColor.AQUA + "Cmd Help: /bswear help");

                } else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    sendMessage(sender, m.prefix + " Usage: /bswear <option>");
                    sendMessage(sender, m.prefix + " Options:");
                    showCommandUsage(sender);
                } else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
                    sendMessage(sender, m.prefix +ChatColor.AQUA+ "BSwear v" +ChatColor.GREEN+ m.version);

                } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
                    List<String> words = m.swears.getStringList("warnList");
                    String word = args[1].toLowerCase();
                    if (!words.contains(word)) {
                        words.add(word);
                        m.swears.set("warnList", words);
                        m.saveSwearConfig();
                        sender.sendMessage(m.prefix + m.getConfig().getString("messages.addword"));
                    } else {
                        sender.sendMessage(m.prefix +ChatColor.RED+ChatColor.BOLD + "Error! This word is already blocked!");
                    }

                } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
                    List<String> words = m.swears.getStringList("warnList");
                    String word = args[1].toLowerCase();
                    if (words.contains(word)) {
                        words.remove(word);
                        m.swears.set("warnList", words);
                        m.saveSwearConfig();
                        sender.sendMessage(m.prefix + m.getConfig().getString("messages.delword"));
                    } else {
                        sender.sendMessage(m.prefix +ChatColor.RED+ChatColor.BOLD+ "Error! This word is not blocked!");
                    }

                } else if (args.length == 1 && args[0].equalsIgnoreCase("wordlist")) {
                    List<String> words = m.swears.getStringList("warnList");
                    String message = "Blocked Words: ";
                    for (String w : words) {
                        message = message + w;
                        if (!(w == words.get(words.size() - 1))) message = message + ", ";
                    }
                    sender.sendMessage(message);

                } else if (args.length == 1 && args[0].equalsIgnoreCase("swearers")) {
                    Set<String> keys = m.swearers.getConfigurationSection("swearers").getKeys(false);
                    sender.sendMessage(ChatColor.BLUE + "Swearers:");
                    String swearerList = "";
                    for (String s : keys) swearerList = swearerList + s +", ";
                    sender.sendMessage(swearerList);

                } else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    List<String> words = m.swears.getStringList("warnList");
                    words.stream().forEach((word) -> {
                        words.remove(word);
                        m.swears.set("warnList", words);
                        m.saveSwearConfig();
                        sender.sendMessage(m.prefix +"All blocked words have been unblocked!");
                    });

                } else if (args.length == 2 && args[0].equalsIgnoreCase("prefix")) {
                    m.getConfig().set("messages.prefix", args[1]);
                    m.saveConfig();
                    m.prefix = ChatColor.translateAlternateColorCodes('&', args[1]+" ");

                } else if (args.length > 0 && args[0].equalsIgnoreCase("useTitles")) {
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("enable")) {
                            m.getConfig().set("sendTitle", true);
                            sendMessage(sender, m.prefix + "Title on swear is now ENABLED");
                        } else {
                            m.getConfig().set("sendTitle", false);
                            sendMessage(sender, m.prefix + "Title on swear is now DISABLED");
                        }
                        m.saveConfig();
                    } else {
                        sendMessage(sender, "TitleAPI enabled: " + m.getConfig().getBoolean("sendTitle"));
                    }
                } else {
                    sender.sendMessage(m.prefix + "Error! Wrong args, do \"/bswear help\" for command help");
                }
            } else {
                sender.sendMessage(m.prefix + "BSwear " + m.version);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', m.getConfig().getString("messages.noperm")));
            }
            return false;
        }
        return false;
    }

    private void showCommandUsage(CommandSender s) {
        String[] options = {
                "add <word> - Blocks word",
                "remove <word> - Unblocks word",
                "clear - Unblocks all words",
                "version - Show plugin version",
                "wordlist - Show all blocked words",
                "prefix - Sets the prefix",
                "swearers - Show all swearers",
                "useTitles - set using title on swear"
                };
        for (String option : options) {
            String[] strs = option.split("-");
            sendMessage(s, ChatColor.GOLD + "/bswear " + strs[0] + ChatColor.GREEN + " - "+strs[1]);
        }
    }
    
    public void sendMessage(CommandSender s, String msg) {
        if (s instanceof Player) s.sendMessage(msg);
        else s.sendMessage(ChatColor.stripColor(msg));
    }
}
