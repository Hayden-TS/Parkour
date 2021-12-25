package me.Hayden.Parkour;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class Utils {

    public static ProtectedRegion getRegion(String rg) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = Bukkit.getWorld(Main.plugin.getJSON().getJSONArray("checkpointsData").getJSONObject(0).getString("worldName"));
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        ProtectedRegion region = regions.getRegion(rg);
        return region;
    }


}
