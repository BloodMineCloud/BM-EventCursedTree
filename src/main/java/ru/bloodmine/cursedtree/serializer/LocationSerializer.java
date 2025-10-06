package ru.bloodmine.cursedtree.serializer;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class LocationSerializer implements TypeSerializer<Location> {

    @Override
    public Location deserialize(Type type, ConfigurationNode node) {
        String world = node.node("world").getString();
        int x = node.node("x").getInt();
        int y = node.node("y").getInt();
        int z = node.node("z").getInt();
        if (world == null) throw new IllegalArgumentException("world is null");
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable Location obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;
        String world = obj.getWorld() != null ? obj.getWorld().getName() : null;
        int x = obj.getBlockX();
        int y = obj.getBlockY();
        int z = obj.getBlockZ();

        if (world == null) throw new IllegalArgumentException("world is null");
        node.node("world").set(world);
        node.node("x").set(x);
        node.node("y").set(y);
        node.node("z").set(z);
    }
}
