package ru.bloodmine.cursedtree.serializer;

import io.leangen.geantyref.TypeToken;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Map;

public class ItemStackSerializer implements TypeSerializer<ItemStack> {
    TypeToken<Map<String, Object>> itemTypeToken = new TypeToken<>() {
    };

    @Override
    public ItemStack deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, Object> item = node.get(itemTypeToken);
        if (item == null) return null;

        return ItemStack.deserialize(item);
    }

    @Override
    public void serialize(Type type, @Nullable ItemStack obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) return;

        node.set(itemTypeToken, obj.serialize());
    }
}
