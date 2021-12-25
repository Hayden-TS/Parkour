package me.Hayden.Parkour;

import fr.mrmicky.fastboard.FastBoard;
import me.Hayden.Parkour.database.Database;
import me.Hayden.Parkour.listeners.LeaveEvent;
import me.Hayden.Parkour.listeners.PlayerMoveEvent;
import me.Hayden.Parkour.listeners.RegionEvents;
import org.apache.commons.io.IOUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Main extends JavaPlugin {
    public static Main plugin;
    public static Map<UUID, FastBoard> boards = new HashMap<>();
    public Database database;
    private File locations;

    public void onEnable() {
        plugin = this;
        locations = new File(getDataFolder().getPath(), "checkpoints.json");
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(), this);
        getServer().getPluginManager().registerEvents(new LeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new RegionEvents(), this);

        database = new Database();
        String connectionString = Main.plugin.getConfig().getString("database.connectionString");

        database.connect(connectionString);


        if (!locations.exists()) {
            try {
                saveCheckpoints();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {

            for (FastBoard board : Main.boards.values()) {
                updateBoard(board);
            }

        }, 0, 200);


    }

    public void onDisable() {

    }

    //Logic for creating json if not exists
    private void saveCheckpoints() throws IOException {
        byte[] buffer = getResource("checkpoints.json").readAllBytes();

        File targetFile = new File(getDataFolder().getPath() + "/checkpoints.json");
        targetFile.createNewFile();
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);

        outStream.close();
    }

    public JSONObject getJSON() {
        InputStream is = null;
        try {
            is = new FileInputStream(locations);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONObject json = null;
        try {
            json = new JSONObject(IOUtils.toString(is));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }


    public void updateBoard(FastBoard board) {
        String string = "no data";
        if (database.playerExists(board.getPlayer().getUniqueId())) {
            string = String.valueOf(database.getBestTime(board.getPlayer().getUniqueId()));
        }
        board.updateLines(
                "",
                "Best Attempt: " + string,
                "",
                "Global:",
                " #1 - " + getKey(database.getBestGlobalTime(0)) + " - " + getValue(database.getBestGlobalTime(0)),
                " #2 - " + getKey(database.getBestGlobalTime(1)) + " - " + getValue(database.getBestGlobalTime(1)),
                " #3 - " + getKey(database.getBestGlobalTime(2)) + " - " + getValue(database.getBestGlobalTime(2)),
                " #4 - " + getKey(database.getBestGlobalTime(3)) + " - " + getValue(database.getBestGlobalTime(3)),
                " #5 - " + getKey(database.getBestGlobalTime(4)) + " - " + getValue(database.getBestGlobalTime(4))
        );
    }

    private String getKey(Map.Entry<OfflinePlayer, Long> entry) {
        if (entry == null) {
            return "no data";
        }
        return entry.getKey().getName();
    }

    private String getValue(Map.Entry<OfflinePlayer, Long> entry) {
        if (entry == null) {
            return "no data";
        }
        return String.valueOf(entry.getValue());
    }


}
