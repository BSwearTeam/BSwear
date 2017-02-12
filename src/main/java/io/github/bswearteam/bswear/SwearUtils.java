package io.github.bswearteam.bswear;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class SwearUtils {
    private static BSwear main;
    public SwearUtils(BSwear m) {main = m;}

    /**
     * if commandenable is true,
     * then an {@link Command} is run.
     * 
     * @param sc <i>the command to run</i>
     * @param swearer <i>the player that is swearing, in an String format</i>
     */
    public static void runCommand(String sc, String swearer) {
        if (main.getConfig().getBoolean("commandenable")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sc.replace("%swearer%", swearer));
        }
    }

    /**
     * if commandenable is true,
     * then a {@link Command} will run.
     * 
     * @param sc <i>the command to run</i>
     * @param swearer <i>the player that is swearing, in an {@link Player} format</i>
     */
    public static void runCommand(String sc, Player swearer) {
        runCommand(sc, swearer.getName());
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

    /**
     * if kickSwearer is true, this will kick them
     */
    public static void kickSwearer(Player player) {
        if (!(main.getConfig().getBoolean("banSwearer")) && main.getConfig().getBoolean("kickSwearer")) {
            player.kickPlayer("We've detected a swear word MIGHT be in your message so we kicked you ");
        }
    }

    /**
     * if banSwearer is true, this will ban them
     */
    @SuppressWarnings("deprecation")
    public static void banSwearer(Player player) {
        if (main.getConfig().getBoolean("banSwearer") && !(main.getConfig().getBoolean("kickSwearer"))) {
            player.kickPlayer("We've detected a swear word in your msg, so its setup to ban you");
            player.setBanned(true);
        }
    }

    /**
     * Checks every thing.
     */
    public static void checkAll(String sc, Player p) {
        if (hasSweared(p)){
            setSwearNum(p, getPlrSwears(p) + 1);
        }else{
            setSwearNum(p, 1);
        }

        SwearUtils.runCommand(sc, p);
        SwearUtils.sendTitle(p);
        SwearUtils.kickSwearer(p);
        SwearUtils.banSwearer(p);
    }

    /**
     * @param player the Player.
     * @param amount the amount.
     */ 
    public static void setSwearNum(Player plr, int amount) {
        main.swearers.set("swearers."+plr.getName()+".amount", amount);
        main.swearers.set("swearers."+plr.getName()+".hasSweared", true);
    }

    /**
     * @param plr the Player
     * @return The amount of times {@link Player} has sweared.
     */ 
    public static int getPlrSwears(Player plr) {
        return main.swearers.getInt("swearers."+plr.getName()+".amount");
    }

    /**
     * @param plr The Player
     * @return player has sweared befour.
     */
    public static boolean hasSweared(Player plr) {
        return main.swearers.getBoolean("swearers."+plr.getName()+".hasSweared");
    }

    @SuppressWarnings("deprecation")
    public static void sendTitle(Player plr, String title, String sub) {
        TitlesAPI.sendFullTitle(plr, 10, 80, 10, title, sub);
    }
}
