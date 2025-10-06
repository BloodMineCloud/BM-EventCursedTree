package ru.bloodmine.cursedtree.serializer;

import org.bukkit.util.BlockVector;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class BlockVectorSerializer implements TypeSerializer<BlockVector> {
    @Override
    public BlockVector deserialize(Type type, ConfigurationNode node) {
        int x = node.node("x").getInt();
        int y = node.node("y").getInt();
        int z = node.node("z").getInt();

        return new BlockVector(x, y, z);
    }

    @Override
    public void serialize(Type type, @Nullable BlockVector obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.node("x").set(obj.getBlockX());
        node.node("y").set(obj.getBlockY());
        node.node("z").set(obj.getBlockZ());
    }
}
