package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.service.ActivateTreeService;
import ru.bloodmine.cursedtree.service.TreePlacerService;
import ru.bloodmine.cursedtree.util.LifeCyclable;

import java.util.Set;

public class TreeLifeCycle implements LifeCyclable {

    private final ActivateTreeService activateTreeService;
    private final TreePlacerService treePlacerService;
    private final Set<Tree> trees;

    @Inject
    public TreeLifeCycle(ActivateTreeService activateTreeService, TreePlacerService treePlacerService, @Named("trees") Set<Tree> trees) {
        this.activateTreeService = activateTreeService;
        this.treePlacerService = treePlacerService;
        this.trees = trees;
    }

    @Override
    public void load() {
        for (Tree tree : trees) {
            treePlacerService.pasteTree(tree);
        }
    }

    @Override
    public void shutdown() {
        activateTreeService.endCurrentEvent();
        treePlacerService.removeAll();
    }
}
