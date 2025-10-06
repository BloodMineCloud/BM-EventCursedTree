package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.Getter;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;
import ru.bloodmine.cursedtree.model.ActiveTree;
import ru.bloodmine.cursedtree.model.Phase;
import ru.bloodmine.cursedtree.model.Tree;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ActivateTreeService {

    @InjectLogger
    private Logger logger;

    private final Set<Tree> trees;
    private final List<Phase> phases;

    @Getter
    private ActiveTree currentActiveTree;

    @Inject
    public ActivateTreeService(@Named("trees") Set<Tree> trees, @Named("phases") List<Phase> phases) {
        this.trees = trees;
        this.phases = phases;
    }

    public boolean startEvent(Tree tree) {
        endCurrentEvent();

        currentActiveTree = new ActiveTree(logger, tree, phases);
        boolean isNextedPhase = currentActiveTree.next();
        if (isNextedPhase) {
            logger.info("Tree has been started. Phase started: {}", currentActiveTree.getCurrentIndex());
        }
        else {
            logger.warn("The next phase is absent. The tree is stopped");
        }
        return isNextedPhase;
    }

    public Tree startRandomEvent() {
        if (trees.isEmpty()) throw new IllegalArgumentException("No tree found");
        Tree tree = trees.stream().skip(ThreadLocalRandom.current().nextInt(trees.size())).findFirst().get();
        if (startEvent(tree)) return tree;
        else return null;
    }

    public boolean endCurrentEvent() {
        if (currentActiveTree != null) {
            currentActiveTree.shutdown();
            currentActiveTree = null;
            logger.info("Tree has been ended.");
            return true;
        }
        else {
            logger.info("Tree has already ended.");
            return false;
        }
    }
}
