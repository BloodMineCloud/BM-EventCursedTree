package ru.bloodmine.cursedtree.serializer;

import io.leangen.geantyref.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Base64;
import java.util.Map;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    private static final Base64.Encoder ENCODER = Base64.getEncoder();
    private static final Base64.Decoder DECODER = Base64.getDecoder();

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) {
        return ItemStack.deserializeBytes(DECODER.decode(node.getString()));
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.set(ENCODER.encodeToString(obj.serializeAsBytes()));
    }
}
