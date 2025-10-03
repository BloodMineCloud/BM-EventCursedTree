package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import ru.bloodmine.cursedtree.model.Tree;

public class TreePlacerService {
    private final String treeSchematic;
    private final SchematicActor schematicActor;
    private final RegionService regionService;

    @Inject
    public TreePlacerService(@Named("schematicTree") String treeSchematic, SchematicActorFactory schematicActorFactory, RegionService regionService) {
        this.treeSchematic = treeSchematic;
        this.schematicActor = schematicActorFactory.createSchematicActor();
        this.regionService = regionService;

        if (!schematicActor.schemaExist(treeSchematic)) throw new IllegalArgumentException("Schematic '" + treeSchematic + "' does not exist");
    }

    public void pasteTree(Tree tree) {
        schematicActor.paste(treeSchematic, tree.id(), tree.spawnLocation());
        regionService.setRegion(tree.id(), tree.spawnLocation());
    }

    public void removeTree(Tree tree) {
        schematicActor.undoSchematic(tree.id());
        regionService.removeRegion(tree.id());
    }
}
