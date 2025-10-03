package ru.bloodmine.cursedtree.model.parser;

import ru.bloodmine.cursedtree.model.action.Action;
import ru.bloodmine.cursedtree.model.action.SchemAction;
import ru.bloodmine.cursedtree.service.SchematicActorFactory;

/**
 *
 */
public class SchemParser implements ActionParser<SchemAction> {
    private final SchematicActorFactory schematicActorFactory;

    public SchemParser(SchematicActorFactory schematicActorFactory) {
        this.schematicActorFactory = schematicActorFactory;
    }

    @Override
    public boolean isValidSyntaxBody(String body) {
        return !body.isBlank();
    }

    @Override
    public SchemAction parse(String body) {
        return new SchemAction(schematicActorFactory, body);
    }
}
