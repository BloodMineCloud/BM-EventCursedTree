package ru.bloodmine.cursedtree.model;

import org.bukkit.Location;

public record Tree(
        String id,
        Location spawnLocation
) {

    public int x() {
        return spawnLocation.getBlockX();
    }

    public int y() {
        return spawnLocation.getBlockY();
    }

    public int z() {
        return spawnLocation.getBlockZ();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tree tree)) return false;

        return id.equals(tree.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result;
        return result;
    }
}
