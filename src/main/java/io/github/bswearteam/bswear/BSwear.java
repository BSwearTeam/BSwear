package io.github.bswearteam.bswear;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
 
public class BSwear extends JavaPlugin implements Listener {
    public PluginDescriptionFile pdf = Bukkit.getServer().getPluginManager().getPlugin("BSwear").getDescription();
    public String version = pdf.getVersion();
    public String about = pdf.getDescription();
	
    public static Permission BypassPerm = new Permission("bswear.bypass");
    public Permission COMMAND_PERM      = new Permission("bswear.command.use");
    public Permission allPerm           = new Permission("bswear.*");
    public FileConfiguration config     = new YamlConfiguration();
    public FileConfiguration swears     = new YamlConfiguration();
    public FileConfiguration swearers   = new YamlConfiguration();
    public FileConfiguration muted      = new YamlConfiguration();
    public String prefix = ChatColor.GOLD + "[BSwear] "+ChatColor.GREEN;
    private File configf, swearf, swearersf, mutedf;

    /**
     * code thats ran when BSwear is enabled
     * 
     * @author The BSwear Team
     * */
    public void onEnable() {
    	PluginManager pm = Bukkit.getServer().getPluginManager();
    	pm.addPermission(BypassPerm);
        pm.addPermission(COMMAND_PERM);

        configf = new File(getDataFolder(), "config.yml");
        swearf = new File(getDataFolder(), "words.yml");
        swearersf = new File(getDataFolder(), "swearers.yml");
        mutedf = new File(getDataFolder(), "mutedPlayers.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!swearf.exists()) {
            swearf.getParentFile().mkdirs();
            saveResource("words.yml", false);
        }
        if (!swearersf.exists()) {
            swearersf.getParentFile().mkdirs();
            saveResource("swearers.yml", false);
        }
        if (!mutedf.exists()) {
            mutedf.getParentFile().mkdirs();
            saveResource("mutedPlayers.yml", false);
        }

        try {
            config.load(configf);
            swears.load(swearf);
            swearers.load(swearersf);
            muted.load(mutedf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        saveDefaultConfig();
        pm.registerEvents(this, this);

        // Shows an message saying BSwear is enabled
        if (getConfig().getBoolean("showEnabledMessage")) {
        	getLogger().info("");
        	getLogger().info("[=-=-=] the BSwear Team [=-=-=]");
        	getLogger().info("This server runs BSwear version "+version);
        	getLogger().info("BSwear uses the ClusterAPI (by AdityaTD)");
        	getLogger().info("");
        }

        // Checks if both ban and kick are set to true
        if (getConfig().getBoolean("banSwearer") == true && getConfig().getBoolean("kickSwearer") == true) {
            getLogger().info("[ERROR] You can not have, both ban and kick set to true! setting ban to false...");
            getConfig().set("banSwearer", false);
        }

        // sets the prefix
        if (getConfig().getString("messages.prefix") == null) {
            getConfig().set("messages.prefix", "&6[BSwear]&2");
        } else {
            prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")) + " ";
        }
        
        String jversion = System.getProperty("java.version").split("_")[0].replace("1.", "").replace(".0", "");
        if (Integer.parseInt(jversion) < 8) {
           getLogger().info("Outdated Java! BSwear will require Java 8 in an furture update!");
        }
        
        getCommand("mute").setExecutor(new Mute(this));
        registerEvents(this, this, new OnJoin(this), new Mute(this), new Advertising(this));
    }

    /*Controls config files*/
    public FileConfiguration getSwearConfig() { return this.swears; }
    public void saveSwearConfig() { saveConf(swears, swearf); }

    public FileConfiguration getSwearersConfig() { return this.swearers; }
    public void saveSwearersConfig() { saveConf(swearers, swearersf); }
    
    public FileConfiguration getMutedConfig() { return this.muted; }
    public void saveMutedConfig() { saveConf(muted, mutedf); }

    /**
     * The swear blocker
     * 
     * @author BSwear Team
     */
    @EventHandler
    public void onChatSwear(AsyncPlayerChatEvent event) {
    	if (!event.getPlayer().hasPermission(BypassPerm)) {
    		String message = replaceAllNotNormal(event.getMessage().toLowerCase().replaceAll("[%&*()$#!-_@]", ""));

    		for (String word : getSwearConfig().getStringList("warnList")) {
    		    boolean a = false;

    		    String[] messageAsArray = message.split(" ");

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

                if (a || message.contains(" "+word+" ") || message.contains(" "+word) || message.contains(word+" ") || message == word) {

                    if (getConfig().getBoolean("cancelMessage") == true) {
    					event.setCancelled(true); // Cancel Message
    				} else {
    				    String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
    				    event.setMessage(messagewithoutswear);
    				    event.getPlayer().sendMessage(ChatColor.DARK_GREEN+"[BSwear] "+ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD +"We've detected a swear word MIGHT be in your message so we blocked that word!");
                    }
    				
    			    // The flowing Will check the config, to see if the user has it enabled :)
    				SwearUtils.checkAll(getConfig().getString("command"), event.getPlayer());
    			}
    		}
    	}
    }

    /**
     * @author The BSwear Team
     */
    public void onDisable(){
    	getLogger().info("BSwear is now disabled");
    }


    /**
     * @author BSwear Team
     */
    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
    	for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }


    /**
     * The /bswear command for BSwear
     * 
     * @author BSwear Team
     * @since v2.0
     */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("bswear")) {
    		if (sender.hasPermission(COMMAND_PERM) || sender.isOp() || sender.hasPermission(allPerm)) {
    			if (args.length == 0) {
    			    sender.sendMessage(prefix);
    			    sender.sendMessage(ChatColor.AQUA + "BSwear is an antiswearing plugin for Minecraft,");
                 	sender.sendMessage(ChatColor.AQUA + "Cmd Help: /bswear help");

    			} else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(prefix + " Command Help!");
                    sender.sendMessage(ChatColor.GOLD + "/bswear add <word>" + ChatColor.GREEN + " - Blocks an word");
                    sender.sendMessage(ChatColor.GOLD + "/bswear remove <word>" + ChatColor.GREEN + " - Unblocks an word");
                    sender.sendMessage(ChatColor.GOLD + "/bswear clear" + ChatColor.GREEN + " - Unblocks all words");
                    sender.sendMessage(ChatColor.GOLD + "/bswear version" + ChatColor.GREEN + " - Shows the version");
                    sender.sendMessage(ChatColor.GOLD + "/bswear wordlist" + ChatColor.GREEN + " - Shows the blocked words");
                    sender.sendMessage(ChatColor.GOLD + "/bswear prefix" + ChatColor.GREEN + " - Set the message prefix");
                    sender.sendMessage(ChatColor.GOLD + "/bswear swearers" + ChatColor.GREEN + " - Show all players who sweared");

    			} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
    				List<String> words = getSwearConfig().getStringList("warnList");
                 		String word = args[1].toLowerCase();
                 		if (!words.contains(word)) {
                 			words.add(word);
                 			getSwearConfig().set("words", words);
                 			saveSwearConfig();
                 			sender.sendMessage(prefix + getConfig().getString("messages.addword"));
                 		} else {
                 			sender.sendMessage(prefix + ChatColor.RED + ChatColor.BOLD + "Error! This word is already blocked!");
                 		}

    			} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
    				List<String> words = getSwearConfig().getStringList("warnList");
    				String word = args[1].toLowerCase();
    				if (words.contains(word)) {
                	    words.remove(word);
                	    getSwearConfig().set("warnList", words);
                	    saveSwearConfig();
                	    sender.sendMessage(prefix + getConfig().getString("messages.delword"));
    				} else {
    					sender.sendMessage(prefix + ChatColor.RED + ChatColor.BOLD + "Error! This word is not blocked!");
    				}

    			} else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
    				sender.sendMessage(ChatColor.GOLD + "Version:" + ChatColor.GREEN + "BSwear v"+version);

    			} else if (args.length == 3 && args[0].equalsIgnoreCase("mute")) {
    			    ((Player) sender).performCommand("mute "+args[1]+" "+args[2]);

    			} else if (args.length == 1 && args[0].equalsIgnoreCase("wordlist")) {
    			    List<String> words = getSwearConfig().getStringList("warnList");
    			    String message = "Blocked Words: ";
    		        for (String w : words) {
    		            message = message + w;
    		            
    		            if (!(w == words.get(words.size() - 1))) {
    		                message = message + ", ";
    		            }
    		        }
    			    sender.sendMessage(message);

    			} else if (args.length == 1 && args[0].equalsIgnoreCase("swearers")) {
    			    Set<String> keys = getSwearersConfig().getConfigurationSection("swearers").getKeys(false);
                    sender.sendMessage(ChatColor.BLUE + "Swearers:");
                    String swearerList = "";
                    for (String s : keys) {
                        swearerList = swearerList + s + ", ";
                    }
                    sender.sendMessage(swearerList);

    			} else if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
    			    List<String> words = getSwearConfig().getStringList("warnList");
    			    for (String word : words) {
    			        words.remove(word);
                        getSwearConfig().set("warnList", words);
                        saveSwearConfig();
                        sender.sendMessage(prefix + "All blocked words have been unblocked!");
    			    }

    			} else if (args.length == 2 && args[0].equalsIgnoreCase("prefix")) {
    			    getConfig().set("messages.prefix", args[1]);
    			    saveConfig();
    			    prefix = ChatColor.translateAlternateColorCodes('&', args[1] + " ");
    
    			} else {
    				sender.sendMessage(prefix + "Error! please check your args OR do \"/bswear help\" for command help");
    			}
    		} else {
    			sender.sendMessage(prefix + getConfig().getString("messages.noperm"));
    		}
    		return false;
    	}
    	return false;
    }


	/**
	 * Replaces all --> ! @ # $ % ^ & * ( ) _ + = - ; ' ] [ . , , | ? < > : "
	 */
	public String replaceAllNotNormal(String str) {
        return str.replaceAll("[^\\p{L}\\p{Nd}]", "").replaceAll("[ * . - = + : ]", "");
    }


	/**
	 * Save config file!
	 */
	public static void saveConf(FileConfiguration config, File file) {
	    try {
	        config.save(file);
	    } catch (IOException e) {
	        System.out.println("[BSwear][ERROR] Cant save file "+file.getName()+"! Error message: "+e.getMessage());
        } 
	}
}
