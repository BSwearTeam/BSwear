package io.github.bswearteam.bswear;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BSwear extends JavaPlugin implements Listener {
    public String version = this.getDescription().getVersion();

    public FileConfiguration config = new YamlConfiguration();
    public FileConfiguration swears = new YamlConfiguration();
    public FileConfiguration swearers = new YamlConfiguration();
    public FileConfiguration muted = new YamlConfiguration();
    public FileConfiguration log = new YamlConfiguration();
    public String prefix = ChatColor.GOLD + "[BSwear] " + ChatColor.GREEN;
    public File configf, swearf, swearersf, logFile, mutedf;
    public ArrayList<String> logtext = new ArrayList<>();
    public ArrayList<String> a = new ArrayList<>();

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        configf = new File(getDataFolder(), "config.yml");
        swearf = new File(getDataFolder(), "words.yml");
        swearersf = new File(getDataFolder(), "swearers.yml");
        mutedf = new File(getDataFolder(), "mutedPlayers.yml");
        logFile = new File(getDataFolder(), "log.yml");

        resourceSave(configf, "config.yml");
        resourceSave(swearf, "words.yml");
        resourceSave(swearersf, "swearers.yml");
        resourceSave(mutedf, "mutedPlayers.yml");

        try {
            logFile.createNewFile();
        } catch (IOException e) { e.printStackTrace(); }

        try {
            config.load(configf);
            swears.load(swearf);
            swearers.load(swearersf);
            muted.load(mutedf);
            log.load(logFile);
        } catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }

        saveDefaultConfig();

        // Shows an message saying BSwear is enabled
        if (getConfig().getBoolean("showEnabledMessage")) {
            getLogger().info("[=-=] BSwear team [=-=]");
            getLogger().info("This server runs BSwear v" + version);
            getLogger().info("- ClusterAPI by AdityaTD");
        }

        // Checks if both ban and kick are set to true
        if (getConfig().getBoolean("banSwearer") && getConfig().getBoolean("kickSwearer")) {
            getConfig().set("banSwearer", false);
            saveConfig();
        }

        // sets the prefix
        getConfig().addDefault("messages.prefix", "&6[BSwear]&2");
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("messages.prefix")) + " ";

        pm.registerEvents(this, this);
        getCommand("mute").setExecutor(new Mute(this));
        getCommand("bswear").setExecutor(new BSwearCommand(this));
        getCommand("swear").setExecutor(new SwearCommand(this));

        Listener[] ls = {this, new OnJoin(this), new Mute(this), new Advertising(this), new AntiSpam(this), new AntiCaps()};
        for (Listener l : ls) pm.registerEvents(l, this);

        checkForUpdate();
    }

    public void checkForUpdate() {
        BufferedInputStream in = null;
        String output = "";

        try {
            in = new BufferedInputStream(new URL("https://raw.githubusercontent.com/BSwearTeam/BSwear/master/version").openStream());
            byte[] contents = new byte[1024];

            int bytesRead = 0;
            while ((bytesRead = in.read(contents)) != -1) output = new String(contents, 0, bytesRead);
        } catch (IOException e) { e.printStackTrace(); }

        String[] ver = output.split("\n");
        if (!ArrayUtils.contains(ver, version)) {
            getLogger().info("[=-=] BSwear Update [=-=]");
            getLogger().info("An Update should be available.");
            getLogger().info("Current Version: " + version);
            getLogger().info("New Version: " + ver[0]);
        } else getLogger().info("BSwear is up-to-date");

    }

    @EventHandler
    public void onChatSwear(AsyncPlayerChatEvent event) {
        String message = ChatColor.stripColor(event.getMessage().toLowerCase().replaceAll("[%&*()$#!-_@]", ""));
        Player p = event.getPlayer();
        if (a.contains(p.getName() + "-" + message)) return;

        boolean has = false;
        String messageFixed = message;

        for (String word : swears.getStringList("warnList")) {
            if (ifHasWord(message, word)) {
                has = true;
                event.setCancelled(true); // BSwear handles sending the message so cancel the event.
                if (getConfig().getBoolean("cancelMessage")) has = false; // cancel message.
                else {
                    messageFixed = messageFixed.replaceAll(word, repeat("*", word.length()));
                    event.setMessage(messageFixed);

                    List<String> l = log.getStringList("log");
                    l.add(p.getName() + " said " + word.toUpperCase() + " in: " + event.getMessage());
                    log.set("log", l);
                    saveConf(log, logFile);

                    SwearUtils.runAll(event.getPlayer());
                    event.setCancelled(true);
                }
            }
            if (has) {
                if (!canSee(p)) 
                    p.sendMessage(ChatColor.DARK_GREEN + "[BSwear] " + ChatColor.AQUA + "A word has been blocked in your message.");

                event.setCancelled(true);
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    pl.sendMessage(String.format(event.getFormat(), p.getDisplayName(), canSee(pl) ? event.getMessage() : messageFixed));
                }
                a.add(event.getPlayer().getName() + "-" + message);
                Bukkit.getScheduler().runTaskLater(this, () -> a.remove(event.getPlayer().getName() + message), 2);
            }
        }
    }

    public String repeat(String a, int b) {
        String c = a;
        for (int i = 1; i < b;) {
            c = c + a;
            i++;
        }
        return c;
    }

    public boolean canSee(Player p) {
        try {
            return getConfig().getStringList("allowViewPlayers").contains(p.getName().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean ifHasWord(String message, String word) {
        boolean a = false;
        String[] messageAsArray = message.split("[ ]");
        for (String partOfMessage : messageAsArray) {
            StringBuilder strBuilder = new StringBuilder();
            char[] messageAsCharArray = partOfMessage.toLowerCase().toCharArray();

            for (char character : messageAsCharArray) if (character >= '0' && character <= '9' || character >= 'a' && character <= 'z')
                strBuilder.append(character);

            if (strBuilder.toString().equalsIgnoreCase(word.toLowerCase())) a = true;
        }
        return a;
    }

    public void saveConf(FileConfiguration config, File file) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().info("[ERROR] Cant save " + file.getName());
        }
    }

    private void resourceSave(File file, String fileName) {
        file.getParentFile().mkdirs();
        if (!file.exists()) saveResource(fileName, false);
    }
}