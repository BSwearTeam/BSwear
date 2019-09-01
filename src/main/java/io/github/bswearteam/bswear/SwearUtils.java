package io.github.bswearteam.bswear;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.BanList.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import static io.github.bswearteam.bswear.BSwear.i;

public class SwearUtils {

    public static HashMap<String, Integer> swearers;

    public static void init() {
        swearers = new HashMap<>();
        if (i.swearersf.exists()) {
            load();
        } else save();
    }

    @SuppressWarnings("deprecation")
    public static void runAll(Player p) {
        setSwearNum(p, hasSweared(p) ? getPlrSwears(p) + 1 : 1);

        if (i.getConfig().getBoolean("commandenable")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
            i.getConfig().getString("command").replace("%swearer%", p.getName()));

        if (i.getConfig().getBoolean("sendTitle")) TitlesAPI.sendFullTitle(p, 10, 80, 10, ChatColor.DARK_RED + "ERROR", ChatColor.GOLD + "No Swearing");

        try {
            boolean kick = i.getConfig().getBoolean("kickSwearer");
            boolean ban = i.getConfig().getBoolean("banSwearer");

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
        String uuid = plr.getUniqueId().toString();
        swearers.put(uuid, swearers.getOrDefault(uuid, 0) + 1);
        save();
    }

    public static int getPlrSwears(Player plr) {
        return hasSweared(plr) ? swearers.get(plr.getUniqueId().toString()) : 0;
    }

    public static boolean hasSweared(Player plr) {
        return swearers.keySet().contains(plr.getUniqueId().toString());
    }
    
    @SuppressWarnings("unchecked")
    public static void load() {
        try {
            i.swearersf.getParentFile().mkdir();
            FileInputStream fis = new FileInputStream(i.swearersf);
            ObjectInputStream ois = new ObjectInputStream(fis);
            swearers = (HashMap<String, Integer>) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            i.swearersf.createNewFile();
            FileOutputStream fos = new FileOutputStream(i.swearersf);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(swearers);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}