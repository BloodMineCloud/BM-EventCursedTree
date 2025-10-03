package ru.bloodmine.cursedtree.serializer;

import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.function.Predicate;

public class TextComponentSerializer extends ScalarSerializer<TextComponent> {

    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();

    public TextComponentSerializer() {
        super(TextComponent.class);
    }

    @Override
    public TextComponent deserialize(Type type, Object obj) throws SerializationException {
        if (!(obj instanceof String str)) throw new SerializationException("Expected a string but got " + obj.getClass());
        return serializer.deserialize(str);
    }

    @Override
    protected Object serialize(TextComponent item, Predicate<Class<?>> typeSupported) {
        if (!typeSupported.test(String.class)) return null;
        return serializer.serialize(item);
    }
}
