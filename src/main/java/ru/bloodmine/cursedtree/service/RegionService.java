package ru.bloodmine.cursedtree.service;

import com.google.inject.name.Named;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class RegionService {
    private static final String NAME_TEMPLATE = "tree_id-%s_hash-%s";
    private static final String SYMBOLS_ID = "abcdefghijklmnopqrstuvwxyz1234567890";
    private static String RANDOM_HASH = ThreadLocalRandom.current().ints(4, 0, SYMBOLS_ID.length())
            .mapToObj(SYMBOLS_ID::charAt)
            .map(Object::toString)
            .collect(Collectors.joining());

    private final Map<String, String> sessionNameToWorld = new HashMap<>();
    private final Set<String> activeRegions = new HashSet<>();
    private final double cubeRadius;
    private final Map<Flag<?>, Object> flags;

    public RegionService(@Named("cubeRadius") double cubeRadius, @Named("flags") Map<Flag<?>, Object> flags) {
        this.cubeRadius = cubeRadius;
        this.flags = flags;
    }

    public void removeRegion(String sessionName) {
        String hashedSessionName = hashName(sessionName);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        String worldName = sessionNameToWorld.get(hashedSessionName);
        if (worldName == null) return;
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;
        RegionManager rm = container.get(BukkitAdapter.adapt(world));
        if (rm == null) return;
        rm.removeRegion(hashedSessionName);

        activeRegions.remove(hashedSessionName);
    }

    public void clearRegions() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        sessionNameToWorld.forEach((sessionName, worldName) -> {
            World world = Bukkit.getWorld(worldName);
            if (world == null) return;

            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm == null) return;

            rm.removeRegion(sessionName);
        });

        activeRegions.clear();
    }

    public void setRegion(String sessionName, Location treeLocation) {
        String hashedSessionName = hashName(sessionName);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager rm = container.get(BukkitAdapter.adapt(treeLocation.getWorld()));
        if (rm == null)
            throw new IllegalStateException("No RegionManager for world " + treeLocation.getWorld().getName());

        RegionManager prevRegionManager = null;
        ProtectedRegion prevProtectedRegion = null;

        if (activeRegions.contains(hashedSessionName) && sessionNameToWorld.containsKey(hashedSessionName)) {
            String worldName = sessionNameToWorld.get(hashedSessionName);
            World world = Bukkit.getWorld(worldName);
            if (world == null) return;
            prevRegionManager = container.get(BukkitAdapter.adapt(world));
            if (prevRegionManager != null) {
                prevProtectedRegion = prevRegionManager.getRegion(sessionName);
            }
        }

        // Производим операцию вставки
        try {
            if (prevProtectedRegion != null) {
                prevRegionManager.removeRegion(prevProtectedRegion.getId());
            }

            ProtectedCuboidRegion region = getProtectedCuboidRegion(hashedSessionName, treeLocation);
            rm.addRegion(region);
            sessionNameToWorld.put(treeLocation.getWorld().getName(), region.getId());
            activeRegions.add(hashedSessionName);
        } catch (Exception e) {
            //Откат назад в случае ошибки
            if (prevProtectedRegion != null) {
                prevRegionManager.addRegion(prevProtectedRegion);
            }
        }
    }

    private @NotNull ProtectedCuboidRegion getProtectedCuboidRegion(String hashedSessionName, Location treeLocation) {
        int minY = treeLocation.getWorld().getMinHeight();
        int maxY = treeLocation.getWorld().getMaxHeight() - 1;

        BlockVector3 loc1 = BlockVector3.at(treeLocation.getX() - cubeRadius, minY, treeLocation.getZ() - cubeRadius);
        BlockVector3 loc2 = BlockVector3.at(treeLocation.getX() + cubeRadius, maxY, treeLocation.getZ() + cubeRadius);

        ProtectedCuboidRegion region = new ProtectedCuboidRegion(
                hashedSessionName,
                loc1,
                loc2
        );
        region.setFlags(flags);
        return region;
    }

    private static String hashName(String id) {
        return String.format(NAME_TEMPLATE, id, RANDOM_HASH);
    }
}
