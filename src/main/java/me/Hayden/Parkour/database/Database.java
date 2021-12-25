package me.Hayden.Parkour.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class Database {

    private MongoCollection<Document> players;
    private MongoDatabase db;
    private MongoClient client;


    public void connect(String constr) {
        ConnectionString connectionString = new ConnectionString(constr);
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        client = mongoClient;
        db = mongoClient.getDatabase("minecraft");
        players = db.getCollection("players");
    }

    public void disconnect() {
        client.close();
    }


    public boolean playerExists(UUID uuid) {
        Iterator<Document> it = players.aggregate(Arrays.asList(new Document("$match", new Document("_id", uuid.toString())))).iterator();
        return it.hasNext();
    }

    public void addTime(UUID uuid, Long timemillis) {
        if (playerExists(uuid)) {
            Document document = players.find(new Document("_id", uuid.toString())).first();
            JSONObject json = new JSONObject(JSON.serialize(document));
            JSONArray array = json.getJSONArray("times");
            array.put(timemillis);
            Bson updateOperation = set("times", array);
            Bson filter = eq("_id", uuid.toString());
            players.updateOne(filter, updateOperation);
            return;
        }
        JSONArray array = new JSONArray();
        array.put(timemillis);
        Document player = new Document("_id", uuid.toString()).append("times", array);
        players.insertOne(player);
    }

    public Long getBestTime(UUID uuid) {
        Document player = players.find(new Document("_id", uuid.toString())).first();
        JSONObject json = new JSONObject(JSON.serialize(player));
        JSONArray array = json.getJSONArray("times");
        //Converting JSONArray list to normal array list
        List<Long> list = new ArrayList();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getLong(i));
        }
        Collections.sort(list);
        return list.get(0);
    }


    public Map.Entry<OfflinePlayer, Long> getBestGlobalTime(Integer place) {
        HashMap<OfflinePlayer, Long> map = new HashMap<>();
        MongoCursor cursor = db.getCollection("players").find().iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            if (document != null) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(document.getString("_id")));
                Long time = getBestTime(player.getUniqueId());
                map.put(player, time);
            }

        }

        LinkedHashMap<OfflinePlayer, Long> sortedMap = new LinkedHashMap<>();

        map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEachOrdered(x -> {
                    sortedMap.put(x.getKey(), x.getValue());
                });
        Iterator<Map.Entry<OfflinePlayer, Long>> it = sortedMap.entrySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            if (i == place) {
                return it.next();
            }
            i++;
            it.next();
        }
        return null;
    }


}
