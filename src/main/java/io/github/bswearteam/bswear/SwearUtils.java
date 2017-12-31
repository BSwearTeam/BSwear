package io.github.bswearteam.bswear;

import java.util.Date;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SwearUtils {
    private static BSwear main;
    public SwearUtils(BSwear m) {main = m;}

    /**
     * Checks every thing.
     */
    public static void checkAll(String sc, Player p) {
        try {
            if (hasSweared(p)){
                setSwearNum(p, (getPlrSwears(p) + 1));
            } else {
                setSwearNum(p, 1);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        try {
            runCommand(sc, p);
        } catch (Exception e) { System.err.println("Error: " + e); }

        try {
            sendTitle(p);
        } catch (Exception e) { System.err.println("Error: " + e); }

        try {
            kickOrBan(p);
        } catch (Exception e) { System.err.println("Error: " + e); }
    }

    /**
     * if commandenable is true,
     * then a {@link Command} will run.
     * 
     * @param sc <i>The command to run</i>
     * @param plr <i>The player that is swearing</i>
     */
    public static void runCommand(String sc, Player plr) {
        if (main.getConfig().getBoolean("commandenable")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sc.replace("%swearer%", plr.getName()));
        }
    }

    /**
     * if sendTitle is true,
     * the swearer will get an message as an "title" in the middle of there screen, saying No Swearing
     */
    public static void sendTitle(Player plr) {
        if (main.getConfig().getBoolean("sendTitle")) {
            sendTitle(plr, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "No Swearing");
        }
    }
    
    @SuppressWarnings("deprecation")
    public static void kickOrBan(Player plr) {
        boolean kick = main.getConfig().getBoolean("kickSwearer");
        boolean ban = main.getConfig().getBoolean("banSwearer");
        
        if (kick) {
            plr.kickPlayer("Kicked for swearing");
            return;
        }

        if (ban) {
            plr.kickPlayer("We've detected a swear word in your msg, so your now tempbanned.");
            Date d = new Date(System.currentTimeMillis());
            d.setHours(d.getHours() + getPlrSwears(plr));

            String time = getPlrSwears(plr) + " hour";
            if (getPlrSwears(plr) > 1) time = time + "s";

            Bukkit.getServer().getBanList(Type.NAME).addBan(plr.getName(), "Banned for " + time + " for swearing.", d, "BSwear");
        }
    }

    /**
     * @param player the Player.
     * @param amount the amount.
     */ 
    public static void setSwearNum(Player plr, int amount) {
        main.swearers.set("swearers."+plr.getUniqueId()+".amount", amount);
        main.swearers.set("swearers."+plr.getUniqueId()+".hasSweared", true);
    }

    /**
     * @param plr the Player
     * @return The amount of times {@link Player} has sweared.
     */ 
    public static int getPlrSwears(Player plr) {
        if (hasSweared(plr)) {
            return main.swearers.getInt("swearers."+plr.getUniqueId()+".amount");
        } else {
            return 0;
        }
    }

    /**
     * @param plr The Player
     * @return player has sweared befour.
     */
    public static boolean hasSweared(Player plr) {
        try {
            if (main.swearers.getConfigurationSection("swearers." + plr.getUniqueId()) == null) {
                return false;
            }
            return main.swearers.getBoolean("swearers." + plr.getUniqueId() + ".hasSweared");
        } catch (Exception ingore) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static void sendTitle(Player plr, String title, String sub) {
        try {
            TitlesAPI.sendFullTitle(plr, 10, 80, 10, title, sub);
        } catch (Exception e) {
            // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/bukkit/commits/5ce44d45312122ecf210dbf614e125f72289f43f
            plr.sendTitle(title, sub);
        }
    }

    /**
     * Replaces a word in the message with stars.
     * @return String
     */
    public static String repl(String msg, String w) {
        return msg.replaceAll(w, repeat("*", w.length()));
    }
    
    public static String repeat(String a, int b) {
        String c = a;
        for (int i = 1; i < b;) {
            c = c + a;
            i++;
        }
        return c;
    }

    public static boolean canSee(Player p) {
        try {
            return main.getConfig().getStringList("allowViewPlayers").contains(p.getName().toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
