package io.github.bswearteam.bswear;
 
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoin implements Listener {

    private BSwear m;
    public OnJoin(BSwear main){this.m = main;}

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!m.getConfig().getBoolean("showJoinMessage"))
            return;

        int pWarn = SwearUtils.getPlrSwears(e.getPlayer());

        if (pWarn > 0)
            e.getPlayer().sendMessage(ChatColor.GRAY+ "You have " + pWarn + "/" + m.getConfig().getInt("maxWarnings") + " warnnings!");
    }

}