package me.Hayden.Parkour.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.mrmicky.fastboard.FastBoard;
import me.Hayden.Parkour.Main;
import me.Hayden.Parkour.Utils;
import me.Hayden.Parkour.core.Session;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEvent implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        if (!Session.getSession.containsKey(e.getPlayer().getUniqueId())) {
            return;
        }
        Session.getSession.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        ProtectedRegion region = Utils.getRegion(Main.plugin.getConfig().getString("settings.region"));
        boolean bool = region.contains(e.getPlayer().getLocation().getBlockX(), e.getPlayer().getLocation().getBlockY(), e.getPlayer().getLocation().getBlockZ());
        if (bool) {
            FastBoard board = new FastBoard(e.getPlayer());
            board.updateTitle(ChatColor.DARK_PURPLE + "Parkour");
            Main.boards.put(e.getPlayer().getUniqueId(), board);
            Main.plugin.updateBoard(board);
        }
    }

}
