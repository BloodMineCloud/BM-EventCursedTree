package ru.bloodmine.cursedtree.model.action;

import org.slf4j.Logger;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.service.SchematicActorFactory;
import ru.bloodmine.cursedtree.service.SchematicActor;

public class SchemAction implements Action {
    private final Logger logger;
    private final SchematicActor schematicActor;
    private final String schematicName;
    private Tree tree;

    public SchemAction(Logger logger, SchematicActorFactory schematicActorFactory, String schematicName) {
        this.logger = logger;
        this.schematicActor = schematicActorFactory.createSchematicActor();
        this.schematicName = schematicName;

        boolean isLoadedSchem = schematicActor.loadSchematic(schematicName);
        if (!isLoadedSchem) throw new IllegalArgumentException("Schematic '" + schematicName + "' not load or not found");
    }

    @Override
    public void start(Tree tree) {
        this.tree = tree;
        schematicActor.paste(schematicName, tree.id(), tree.spawnLocation());
        logger.info("Schematic '{}' for tree {} pasted in location {}", schematicName, tree.id(), tree.spawnLocation());
    }

    @Override
    public void stop() {
        if (tree != null) {
            schematicActor.undoSchematic(tree.id());
            logger.info("Schematic for tree {} has been undo", tree.id());
        }
        tree = null;
    }
}
