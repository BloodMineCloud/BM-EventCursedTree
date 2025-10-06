package ru.bloodmine.cursedtree.model.parser;

import com.google.inject.Inject;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;
import ru.bloodmine.cursedtree.model.action.Action;
import ru.bloodmine.cursedtree.model.action.SchemAction;
import ru.bloodmine.cursedtree.service.SchematicActorFactory;

/**
 *
 */
public class SchemParser implements ActionParser<SchemAction> {
    private final SchematicActorFactory schematicActorFactory;

    @InjectLogger
    private Logger logger;

    @Inject
    public SchemParser(SchematicActorFactory schematicActorFactory) {
        this.schematicActorFactory = schematicActorFactory;
    }

    @Override
    public String name() {
        return "schem";
    }

    @Override
    public boolean isValidSyntaxBody(String body) {
        return !body.isBlank();
    }

    @Override
    public SchemAction parse(String body) {
        return new SchemAction(logger, schematicActorFactory, body);
    }
}
