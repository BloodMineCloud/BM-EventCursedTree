package ru.bloodmine.cursedtree.model.parser;

import lombok.Builder;
import lombok.Singular;
import org.jetbrains.annotations.Nullable;
import ru.bloodmine.cursedtree.model.action.Action;

import java.util.HashMap;
import java.util.Map;

@Builder
public class ActionParserContainer {
    @Singular
    private final Map<String, ActionParser<? extends Action>> actionParsers = new HashMap<>();
    
    @Nullable
    public Action tryParse(String name, String body) throws IllegalArgumentException {
        ActionParser<?> actionParser = actionParsers.get(name.toLowerCase());
        if (actionParser == null) return null;
        if (!actionParser.isValidSyntaxBody(body)) throw new IllegalArgumentException("Invalid body '" + body + "' from parser '" + name + "'");
        return actionParser.parse(body);
    }
}
