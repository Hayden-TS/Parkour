package me.Hayden.Parkour.listeners;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.mrmicky.fastboard.FastBoard;
import me.Hayden.Parkour.Main;
import me.Hayden.Parkour.Utils;
import me.Hayden.Parkour.core.Session;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class RegionEvents implements Listener {

    private void logic(PlayerMoveEvent e) {
        ProtectedRegion region = Utils.getRegion(Main.plugin.getConfig().getString("settings.region"));
        boolean getToInRegion = false;
        boolean getFromInRegion = region.contains(e.getFrom().getBlockX(), e.getFrom().getBlockY(), e.getFrom().getBlockZ());

        if (region.contains(e.getTo().getBlockX(), e.getTo().getBlockY(), e.getTo().getBlockZ())) {
            getToInRegion = true;
        }

        if (getFromInRegion == false && getToInRegion == true) {
            //Player entered region
            FastBoard board = new FastBoard(e.getPlayer());
            board.updateTitle(ChatColor.DARK_PURPLE + "Parkour");
            Main.boards.put(e.getPlayer().getUniqueId(), board);
            Main.plugin.updateBoard(board);
        }

        if (getFromInRegion == true && getToInRegion == false) {
            if (Session.getSession.containsKey(e.getPlayer().getUniqueId())) {
                e.getPlayer().setFallDistance(0);
                float yaw = e.getPlayer().getLocation().getYaw();
                float pitch = e.getPlayer().getLocation().getPitch();
                Location lastCheckpoint = Session.getSession.get(e.getPlayer().getUniqueId()).getLastCheckpoint();
                lastCheckpoint.setPitch(pitch);
                lastCheckpoint.setYaw(yaw);
                e.getPlayer().teleport(lastCheckpoint);
                return;
            }
            FastBoard board = Main.boards.remove(e.getPlayer().getUniqueId());

            if (board != null) {
                board.delete();
            }

        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        logic(e);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        logic(e);
    }

}
