package me.Hayden.Parkour.core;

import me.Hayden.Parkour.Main;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class Session {
    public static HashMap<UUID, Session> getSession = new HashMap<>();
    private final StopWatch stopWatch = new StopWatch();
    public int checkpoint;
    Player player;
    private boolean isStarted;
    private long endTime;
    private Location playerLocation;


    public void setPlayer(Player player) {
        this.player = player;
    }

    public void startSession() {
        if (getSession.containsKey(player.getUniqueId())) {
            return;
        }
        isStarted = true;
        checkpoint = 0;
        stopWatch.start();
        System.out.println("Started course. Stopwatch is counting....");
        player.sendTitle(ChatColor.GREEN + "Course started", null);
        getSession.put(player.getUniqueId(), this);
    }

    public Location getLastCheckpoint() {
        JSONArray array = Main.plugin.getJSON().getJSONArray("checkpointsData");

        HashMap<Location, Integer> checkPoints = new HashMap<>();
        JSONObject object = array.getJSONObject(checkpoint);
        return new Location(Main.plugin.getServer().getWorld(object.getString("worldName")), Math.floor(object.getInt("x")),
                Math.floor(object.getInt("y")), Math.floor(object.getInt("z")) - 1);

    }


    public void endSession() {

        long time = getSession.get(player.getUniqueId()).stopWatch.getTime();
        getSession.remove(player.getUniqueId());
        player.sendTitle(ChatColor.GREEN + "COMPLETED", DurationFormatUtils.formatDurationHMS(time).trim());

        Main.plugin.database.addTime(player.getUniqueId(), time);

    }


}
