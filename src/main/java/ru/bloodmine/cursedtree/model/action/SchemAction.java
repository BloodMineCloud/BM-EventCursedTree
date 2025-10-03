package ru.bloodmine.cursedtree.model.action;

import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.service.SchematicActorFactory;
import ru.bloodmine.cursedtree.service.SchematicActor;

public class SchemAction implements Action {
    private final SchematicActor schematicActor;
    private final String schematicName;
    private Tree tree;

    public SchemAction(SchematicActorFactory schematicActorFactory, String schematicName) {
        this.schematicActor = schematicActorFactory.createSchematicActor();
        this.schematicName = schematicName;

        boolean isLoadedSchem = schematicActor.loadSchematic(schematicName);
        if (!isLoadedSchem) throw new IllegalArgumentException("Schematic '" + schematicName + "' not load or not found");
    }

    @Override
    public void start(Tree tree) {
        this.tree = tree;

        schematicActor.paste(schematicName, tree.id(), tree.spawnLocation());
    }

    @Override
    public void stop() {
        if (tree != null) {
            schematicActor.undoSchematic(tree.id());
        }
        tree = null;
    }
}
