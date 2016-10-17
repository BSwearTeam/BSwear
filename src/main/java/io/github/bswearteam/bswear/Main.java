package io.github.bswearteam.bswear;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.bswearteam.bswear.Advertising;
import io.github.bswearteam.bswear.Mute;
import io.github.bswearteam.bswear.OnJoin;
import io.github.ramidzkh.KodeAPI.api.YamlConf;
 
public class Main extends JavaPlugin implements Listener {
	public String version = "4.0";
	public String versionTag = "devBuild";
	public String about = "BSwear, the #1 Antiswearing plugin for Minecraft, Block 400 swear words!";
	
	public static Permission BypassPerm = new Permission("bswear.bypass");
	public Permission COMMAND_PERM = new Permission("bswear.command.use");
    public Permission allPerm = new Permission("bswear.*");

    public FileConfiguration config = new YamlConfiguration();
    public FileConfiguration swears = new YamlConfiguration();

    String prefix = ChatColor.GOLD + "[BSwear] " + ChatColor.GREEN;
    
    private File configf, swearf;
    
    
    /**
     * code thats ran when BSwear is enabled
     * 
     * @author The BSwear Team
     * */
    public void onEnable() {
    	PluginManager pm = Bukkit.getServer().getPluginManager();
        addPermissions(Bukkit.getServer().getPluginManager());
        
        createFiles();
        saveDefaultConfig();
        
        pm.registerEvents(this, this);
        // for console messages BSwear uses consolelog() and not getlogger(), so we can use colors
        boolean showEnabledMessage = getConfig().getBoolean("showEnabledMessage");
        if (showEnabledMessage) {
        	consolelog(ChatColor.AQUA + "");
        	consolelog(ChatColor.GREEN + "BBB" + ChatColor.AQUA + " The BSwear Team " + ChatColor.GREEN + "BBB");
        	consolelog(ChatColor.AQUA + "This server runs BSwear version "+version);
        	consolelog(ChatColor.AQUA + "BSwear uses the ClusterAPI (by AdityaTD), and the KodeAPI (by Ramidzkh)");
        	consolelog(ChatColor.AQUA + "");
        }
        
        if (getConfig().getBoolean("banSwearer") == true && getConfig().getBoolean("kickSwearer") == true) {
			getLogger().info("[ERROR] You can not have, ban and kick set to true!");
		}

        
        getCommand("mute").setExecutor(new Mute(this));
        registerEvents(this, this, new OnJoin(this), new Mute(this), new Advertising(this));
        
        if (!isServerCompatable()) {
        	getLogger().warning("BSwear has not been tested to run on your version of Minecraft");
        	getLogger().warning("Only: 1.10.x, 1.9.x, 1.8.x, 1.7.x, have been tested to work");
        }
    }
    
    
    String addword = getConfig().getString("messages.addword");
    String delword = getConfig().getString("messages.delword");
    String noperm =  getConfig().getString("messages.noperm");
    String swear_msg = getConfig().getString("messages.swearmsg");
    
    
    
    /**
     * Gets the words.yml file
     * 
     * @author Isaiah Patton
     * */
    public FileConfiguration getSwearConfig() {
        return this.swears;
    }
    
    
    
    
    
    /**
     * Saves the words.yml file
     * 
     * @author Isaiah Patton
     * */
    public void saveSwearConfig() {
    	YamlConf.saveConf(swears, swearf);
    }
    
    
    
    
    
    /**
     * Creates the config.yml and the words.yml
     * 
     * @author Isaiah Patton
     * */
    private void createFiles() {
        configf = new File(getDataFolder(), "config.yml");
        swearf = new File(getDataFolder(), "words.yml");

        if (!configf.exists()) {
            configf.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }
        if (!swearf.exists()) {
            swearf.getParentFile().mkdirs();
            saveResource("words.yml", false);
         }

        try {
            config.load(configf);
            swears.load(swearf);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    /**
     * The swear blocker
     * 
     * @author The BSwear Team
     * @since v1.0
     * */
    @EventHandler
    public void onChatSwear(AsyncPlayerChatEvent event) {
    	Player player = event.getPlayer();
    	if (!player.hasPermission(BypassPerm)) {
    		String msg = event.getMessage().toLowerCase().replaceAll("[%&*()$#!-_@]", "");
    		String sc = getConfig().getString("command");
    		String swearmsg = ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD + "We've detected a swear word MIGHT be in your message so we blocked that word!";
    		for (String word : getSwearConfig().getStringList("warnList")) {
    				if (msg.contains(word) && !msg.toLowerCase().contains("hello")) {
    				
    					if (getConfig().getBoolean("cancelMessage") == true) {
    						event.setCancelled(true);
    					} else {
    						String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
    						event.setMessage(messagewithoutswear);
    						player.sendMessage(swearmsg);
    					}
    				
    					// The flowing Will check the config, to see if the user has it enabled :)
    					SwearUtils.checkAll(sc, player);
    			}
    		}
    	}
    }

    
    
    
    /**
     * @author The BSwear Team
     * */
    public void onDisable(){
    	getLogger().info("--------------------------");
    	getLogger().info("- BSwear is now disabled -");
    	getLogger().info("--------------------------");
    }
    
    
    
    
    /**
     * @author The BSwear Team
     * */
    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
    	for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
    
    
    
    
    
    /**
     * Adds the Permissions to Bukkit
     * 
     * @author Isaiah Patton
     * @since v2.0
     * */    
    public void addPermissions(PluginManager pm) {
        pm.addPermission(BypassPerm);
        pm.addPermission(COMMAND_PERM);
    }
    
    
    /**
     * The /bswear command for BSwear
     * 
     * @author The BSwear Team
     * @since v2.0
     * */
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("bswear")) {
    		if (sender.hasPermission(COMMAND_PERM) || sender.isOp()) {
    			if (args.length == 0) {
    				sender.sendMessage(prefix);
    				sender.sendMessage(ChatColor.AQUA + "BSwear is the #1 antiswearing plugin");
                 	sender.sendMessage(ChatColor.AQUA + "Cmd Help: /bswear help");
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
    				List<String> words = getConfig().getStringList("words");
                 	String word = args[1].toLowerCase();
                 	if (!words.contains(word)) {
                 		words.add(word);
                 		getSwearConfig().set("words", words);
                 		saveSwearConfig();
                 		IfMessageNull();
                 		sender.sendMessage(prefix + addword);
                 	} else {
                 		sender.sendMessage(prefix + ChatColor.RED + ChatColor.BOLD + "Error! This word is already blocked!");
                 	}
    			} else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
    				List<String> words = getConfig().getStringList("words");
    				String word = args[1].toLowerCase();
    				if (words.contains(word)) {
                	   words.remove(word);
                	   getSwearConfig().set("words", words);
                	   saveSwearConfig();
                	   IfMessageNull();
                	   sender.sendMessage(prefix + delword);
    				} else {
    					sender.sendMessage(prefix + ChatColor.RED + ChatColor.BOLD + "Error! This word is not blocked!");
    				}
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
    				sender.sendMessage(ChatColor.GOLD + "/bswear add <word>" + ChatColor.GREEN + " - Blocks an word");
    				sender.sendMessage(ChatColor.GOLD + "/bswear remove <word>" + ChatColor.GREEN + " - Unblocks an word");
    				sender.sendMessage(ChatColor.GOLD + "/bswear version" + ChatColor.GREEN + " - Shows the version");
    			} else if (args.length == 1 && args[0].equalsIgnoreCase("version")) {
    				sender.sendMessage(ChatColor.GOLD + "Version:" + ChatColor.GREEN + "BSwearPlus v" + version);
    			} else {
    				sender.sendMessage(prefix + "Error! please check your args");
    			}
    		} else {
    			sender.sendMessage(prefix + noperm);
    		}
    		return false;
    	}
    	return false;
    }
    
    
    
    
    
    /**
     * Fixes the null messages
     * 
     * @author Isaiah Patton
     * @since v3.1.3
     * @deprecated (the null messages are Now fixed)
     * */
    public void IfMessageNull() {
    	if (addword == null && delword == null) {
    		if (getConfig().getString("addword") == null && getConfig().getString("delword") == null) {
                  addword = "Word Added!";
                  delword = "Word Removed";
            } else {
            	addword = ChatColor.translateAlternateColorCodes('&', getConfig().getString("addword"));
            	delword = ChatColor.translateAlternateColorCodes('&', getConfig().getString("delword"));
            }
    	}
    }

    
    
    /**
     * Gets Bukkit's version
     * 
     * @author MrCookieSlime (Slime Fun)
     * */
    public static String getBukkitVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf(".") + 1);
    }
    
    
    /**
     * Checks if the server is compatable with BSwear!
     * 
     * @author BSwear Team
     * */
    public static boolean isServerCompatable() {
    	return getBukkitVersion().startsWith("v1_11") |//upcoming 1.11
    			getBukkitVersion().startsWith("v1_10")| 
    			getBukkitVersion().startsWith("v1_9") |
    			getBukkitVersion().startsWith("v1_8") | 
    			getBukkitVersion().startsWith("v1_7") | 
    			getBukkitVersion().startsWith("PluginBukkitBridge"); //PluginBukkitBridge is the bukkit plugin loader for Rainbow
    }
    
    
    /**
     * send an message to the console!
     * */
    public void consolelog(String message) {
    	Bukkit.getServer().getConsoleSender().sendMessage(message);
    }
    

}
