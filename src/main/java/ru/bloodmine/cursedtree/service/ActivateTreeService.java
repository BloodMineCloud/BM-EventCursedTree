package ru.bloodmine.cursedtree.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.bloodmine.cursedtree.model.ActiveTree;
import ru.bloodmine.cursedtree.model.Phase;
import ru.bloodmine.cursedtree.model.Tree;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ActivateTreeService {
    private final Set<Tree> trees;
    private final List<Phase> phases;



    @Getter
    private ActiveTree currentActiveTree;

    public ActivateTreeService(Set<Tree> trees, List<Phase> phases) {
        this.trees = trees;
        this.phases = phases;
    }

    public boolean startEvent(Tree tree) {
        endCurrentEvent();

        currentActiveTree = new ActiveTree(tree, phases);
        return currentActiveTree.nextPhase();
    }

    public boolean startRandomEvent() {
        if (trees.isEmpty()) throw new IllegalArgumentException("No tree found");
        Tree tree = trees.stream().skip(ThreadLocalRandom.current().nextInt(trees.size()-1)).findFirst().get();
        return startEvent(tree);
    }

    public void endCurrentEvent() {
        if (currentActiveTree != null) {
            currentActiveTree.shutdown();
        }
        currentActiveTree = null;
    }
}
