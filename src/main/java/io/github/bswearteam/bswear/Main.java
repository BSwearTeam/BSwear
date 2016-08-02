package io.github.bswearteam.bswear;

 import java.util.logging.Logger;
 import org.bukkit.Bukkit;
 import org.bukkit.ChatColor;
 import org.bukkit.Location;
 import org.bukkit.Server;
 import org.bukkit.Sound;
 import org.bukkit.World;
 import org.bukkit.configuration.file.FileConfiguration;
 import org.bukkit.configuration.file.FileConfigurationOptions;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.player.AsyncPlayerChatEvent;
 import org.bukkit.permissions.Permission;
 import org.bukkit.plugin.PluginManager;
 import org.bukkit.plugin.java.JavaPlugin;
 import org.bukkit.util.*;
 
 import org.apache.commons.lang.StringUtils;
 
 //BSwear classes:
 import io.github.bswearteam.bswear.TitlesAPI;
 import io.github.bswearteam.bswear.Mute;
 import io.github.bswearteam.bswear.Advertising;
 import io.github.bswearteam.bswear.OnJoin;
 //BSwear ^
 
 //KodeAPI classes:
 import io.github.ramidzkh.KodeAPI.api.YamlConf;
 //KodeAPI ^
 
 public class Main extends JavaPlugin implements Listener {
    public Permission BypassPerm = new Permission("bswear.bypass");
    public Permission allPerm = new Permission("bswear.*");

    public FileConfiguration config = new YamlConfiguration();
    public FileConfiguration swears = new YamlConfiguration();

    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        addPermissions(Bukkit.getServer().getPluginManager());
        
        createFiles();
        saveDefaultConfig();
        
        pm.registerEvents(this, this);
        getLogger().info(ChatColor.GREEN + "BSwear protects this server!");
        getLogger().info(ChatColor.GREEN + "BSwear uses AdityaTD's Cluster API");
        getLogger().info(ChatColor.GREEN + "BSwear uses Ramidzkh's KodeAPI")
        
        getCommand("mute").setExecutor(new Mute(this));
        registerEvents(this, this, new OnJoin(), new Mute(this), new Advertising(this));
    }
    
     private File configf, swearf;

    public FileConfiguration getSwearConfig() {
        return this.swears;
    }
    
    public void saveSwearConfig() {
    	YamlConf.saveConf(swears, swearf);
    }

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
    
    //Checks what words the players are saying
    @EventHandler
    public void onChatSwear(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();
    if (!player.hasPermission(BypassPerm)) {
         String msg = event.getMessage().toLowerCase().replaceAll("[-_@]", "");
         String sc = getConfig().getString("command");
         String swearmsg = ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.YELLOW + ChatColor.AQUA + ChatColor.BOLD + "We've detected a swear word MIGHT be in your message so we blocked that word!";
         String swearer = player.getName();
         for (String word : getSwearConfig().getStringList("warnList")) {
            if (msg.contains(word)) {
               event.setCancelled(true);
                if (getConfig().getBoolean("cancelMessage") == true) {
                    event.setCancelled(true);
                } else {
                    String messagewithoutswear = event.getMessage().replaceAll(word, StringUtils.repeat("*", word.length()));
                    event.setMessage(messagewithoutswear);
                }
               if (!getConfig().getBoolean("commandenable", false)) {
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sc.replace("%player%", swearer));
                }
               if (getConfig().getBoolean("sendTitle") == true) {
                    TitlesAPI.sendFullTitle(event.getPlayer(), 10, 80, 10, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "Don't Swear!");
                }
            }
         }
      }
   }
    
    public void onDisable(){}
    
    public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }
    public void addPermissions(PluginManager pm) {
        pm.addPermission(BypassPerm);
    }
}
