package io.github.bswearteam.bswear;

import java.util.Date;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SwearUtils {
    private static BSwear main;
    public SwearUtils(BSwear m) {main = m;}

    public static void runAll(Player p) {
        setSwearNum(p, hasSweared(p) ? getPlrSwears(p) + 1 : 1);

        if (main.getConfig().getBoolean("commandenable")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            main.getConfig().getString("command").replace("%swearer%", p.getName()));

        if (main.getConfig().getBoolean("sendTitle")) sendTitle(p, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "No Swearing");

        try {
            boolean kick = main.getConfig().getBoolean("kickSwearer");
            boolean ban = main.getConfig().getBoolean("banSwearer");
            
            if (kick) {
                p.kickPlayer("Kicked for swearing");
                return;
            }

            if (ban) {
                Date d = new Date(System.currentTimeMillis());
                d.setHours(d.getHours() + getPlrSwears(p));

                String time = getPlrSwears(p) + " hour";
                if (getPlrSwears(p) > 1) time = time + "s";

                String reason = "Banned for " + time + " for swearing.";
                p.kickPlayer(reason);
                Bukkit.getServer().getBanList(Type.NAME).addBan(p.getName(), reason, d, "BSwear");
            }
        } catch (Exception e) { System.err.println("Error: " + e); }
    }

    public static void setSwearNum(Player plr, int amount) {
        main.swearers.set("swearers." + plr.getUniqueId() + ".amount", amount);
        main.swearers.set("swearers." + plr.getUniqueId() + ".hasSweared", true);
        main.saveConfig();
    }

    public static int getPlrSwears(Player plr) {
        return (hasSweared(plr) ? main.swearers.getInt("swearers."+plr.getUniqueId()+".amount") : 0);
    }

    public static boolean hasSweared(Player plr) {
        try {
            if (main.swearers.getConfigurationSection("swearers." + plr.getUniqueId()) == null) return false;

            return main.swearers.getBoolean("swearers." + plr.getUniqueId() + ".hasSweared");
        } catch (Exception ingore) { return false; }
    }

    @SuppressWarnings("deprecation")
    public static void sendTitle(Player plr, String title, String sub) {
        TitlesAPI.sendFullTitle(plr, 10, 80, 10, title, sub);
    }
}
