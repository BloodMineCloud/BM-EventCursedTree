package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.util.LifeCyclable;

import java.util.Set;

public class TreePlacerService {
    @InjectLogger
    private Logger logger;

    private final String treeSchematic;
    private final SchematicActor schematicActor;
    private final RegionService regionService;

    @Inject
    public TreePlacerService(@Named("schematicDefault") String treeSchematic, SchematicActorFactory schematicActorFactory, RegionService regionService) {
        this.treeSchematic = treeSchematic;
        this.schematicActor = schematicActorFactory.createSchematicActor();
        this.regionService = regionService;

        if (!schematicActor.schemaExist(treeSchematic)) throw new IllegalArgumentException("Schematic '" + treeSchematic + "' does not exist");
    }

    public void pasteTree(Tree tree) {
        schematicActor.paste(treeSchematic, tree.id(), tree.spawnLocation());
        regionService.setRegion(tree.id(), tree.spawnLocation());
        logger.info("Tree '{}' region and schematic paste", tree.id());
    }

    public void removeTree(Tree tree) {
        schematicActor.undoSchematic(tree.id());
        regionService.removeRegion(tree.id());
        logger.info("Tree '{}' region and schematic remove", tree.id());
    }

    public void removeAll() {
        schematicActor.undoAllSchematics();
        regionService.clearRegions();
        logger.info("All trees remove");
    }
}
