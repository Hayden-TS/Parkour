package me.Hayden.Parkour.listeners;

import me.Hayden.Parkour.Main;
import me.Hayden.Parkour.core.Session;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class PlayerMoveEvent implements Listener {
    @EventHandler
    public void move(org.bukkit.event.player.PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location playerLocation = new Location(e.getPlayer().getWorld(), Math.floor(player.getLocation().getBlockX()),
                Math.floor(player.getLocation().getBlockY()), Math.floor(player.getLocation().getBlockZ()));
        JSONArray array = Main.plugin.getJSON().getJSONArray("checkpointsData");

        HashMap<Location, Integer> checkPoints = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            Location location = new Location(Main.plugin.getServer().getWorld(object.getString("worldName")), Math.floor(object.getInt("x")),
                    Math.floor(object.getInt("y")), Math.floor(object.getInt("z")) - 1);
            checkPoints.put(location, i);
        }

        if (checkPoints.containsKey(playerLocation)) {
            int point = checkPoints.get(playerLocation);

            if (point == 0) {
                Session parkour = new Session();
                parkour.setPlayer(player);
                parkour.startSession();
                return;
            }

            if (point == array.length() - 1) {
                if (!Session.getSession.containsKey(player.getUniqueId())) {
                    return;
                }
                Session.getSession.get(player.getUniqueId()).endSession();
                return;
            }

            if (!Session.getSession.containsKey(player.getUniqueId())) {
                return;
            }

            int checkpoint = Session.getSession.get(player.getUniqueId()).checkpoint;
            if (checkpoint >= point) {
                return;
            }
            Session.getSession.get(player.getUniqueId()).checkpoint = point;
            player.sendTitle(ChatColor.YELLOW + "CHECKPOINT", String.valueOf(Session.getSession.get(player.getUniqueId()).checkpoint));

        }

    }


}
