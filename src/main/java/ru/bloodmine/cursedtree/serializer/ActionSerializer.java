package ru.bloodmine.cursedtree.serializer;

import org.checkerframework.checker.units.qual.A;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.SerializationException;
import ru.bloodmine.cursedtree.config.ActionSettings;
import ru.bloodmine.cursedtree.model.action.Action;

import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActionSerializer extends ScalarSerializer<ActionSettings> {
    private final Pattern ACTION_PATTERN = Pattern.compile("\\[(.+)](?: +)?(.*)");
    private final String ACTION_TEMPLATE = "[%s] %s";

    protected ActionSerializer() {
        super(ActionSettings.class);
    }

    @Override
    public ActionSettings deserialize(Type type, Object obj) throws SerializationException {
        if (obj == null) return null;
        String actionStr = obj.toString().trim();

        Matcher matcher = ACTION_PATTERN.matcher(actionStr);
        if (!matcher.matches()) return null;
        String action = matcher.group(1);
        String body = matcher.group(2);

        return new ActionSettings(action, body);
    }

    @Override
    protected Object serialize(ActionSettings item, Predicate<Class<?>> typeSupported) {
        return String.format(ACTION_TEMPLATE, item.getName().toLowerCase(), item.getBody());
    }
}
