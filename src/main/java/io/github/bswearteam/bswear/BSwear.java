package io.github.bswearteam.bswear;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    public boolean devBuild = true;
    
    public PluginDescriptionFile pdf = this.getDescription();
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
     * code that runs when BSwear is enabled
     * 
     * @author The BSwear Team
     * */
    public void onEnable() {
        if (devBuild) version = "21117b";

        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.addPermission(BypassPerm);
        pm.addPermission(COMMAND_PERM);

        configf = new File(getDataFolder(), "config.yml");
        swearf = new File(getDataFolder(), "words.yml");
        swearersf = new File(getDataFolder(), "swearers.yml");
        mutedf = new File(getDataFolder(), "mutedPlayers.yml");

        resourceSave(configf, "config.yml");
        resourceSave(swearf, "words.yml");
        resourceSave(swearersf, "swearers.yml");
        resourceSave(mutedf, "mutedPlayers.yml");

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
            getLogger().info("[=-=-=] BSwear team [=-=-=]");
            getLogger().info("This server runs BSwear version "+version);
            getLogger().info("BSwear uses the ClusterAPI (by AdityaTD)");
            getLogger().info("");
        }

        // Checks if both ban and kick are set to true
        if (getConfig().getBoolean("banSwearer") == true && getConfig().getBoolean("kickSwearer") == true) {
            getLogger().info("[ERROR] You can not have, both ban and kick enabled! setting ban to false...");
            getConfig().set("banSwearer", false);
        }

        // sets the prefix
        if (getConfig().getString("messages.prefix") == null) {
            getConfig().set("messages.prefix", "&6[BSwear]&2");
        } else {
            prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")) + " ";
        }

        pm.registerEvents(this, this);
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("bswear").setExecutor(new BSwearCommand(this));
        registerEvents(this, this, new OnJoin(this), new Mute(this), new Advertising(this));
    }

    /*Controls config files*/
    public FileConfiguration getSwearConfig() { return this.swears; }
    public void saveSwearConfig() { saveConf(swears, swearf); }
    public void saveSwearersConfig() { saveConf(swearers, swearersf); }
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

                if (a || message == word) {

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
     * Replaces all --> ! @ # $ % ^ & * ( ) _ + = - ; ' ] [ . , , | ? < > : "
     */
    public String replaceAllNotNormal(String str) {
        return str.replaceAll("[^\\p{L}\\p{Nd}]", "").replaceAll("[ * . - = + : ]", "");
    }


    /**
     * Save config file!
     */
    public void saveConf(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            getLogger().info("[ERROR] Cant save file "+file.getName()+"! Error message: "+e.getMessage());
        } 
    }

    protected void showCommandUsage(CommandSender s) {
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
            sendMessage(s, ChatColor.GOLD + "/bswear " + strs[0] + ChatColor.GREEN + " - " + strs[1]);
        }
    }
    
    public void sendMessage(CommandSender s, String message) {
        if (s instanceof Player) s.sendMessage(message);
        else s.sendMessage(ChatColor.stripColor(message));
    }

    private void resourceSave(File file, String fileName) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            saveResource(fileName, false);
        }
    }
}
