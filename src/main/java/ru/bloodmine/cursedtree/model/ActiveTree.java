package ru.bloodmine.cursedtree.model;

import lombok.Getter;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class ActiveTree {
    private final Logger logger;

    @Getter
    private final Tree tree;
    private final List<Phase> phases;
    @Getter
    private int currentIndex = -1;
    private boolean shutdown = false;

    public ActiveTree(Logger logger, Tree tree, List<Phase> phases) {
        this.logger = logger;
        this.phases = phases;
        this.tree = tree;
    }

    public boolean hasCurrent() { return currentIndex >= 0 && currentIndex < phases.size(); }
    public boolean hasNext()    { return (currentIndex + 1) < phases.size(); }
    public boolean hasPrev()    { return (currentIndex - 1) >= 0; }
    public boolean isFirst()    { return currentIndex == 0; }
    public boolean isLast()     { return currentIndex == phases.size() - 1; }

    public Optional<String> currentPhaseName() {
        return hasCurrent() ? java.util.Optional.ofNullable(phases.get(currentIndex).getPhaseName())
                : java.util.Optional.empty();
    }

    public boolean next() {
        if (shutdown || !hasNext()) return false;
        enter(currentIndex+1);
        return true;
    }

    public boolean prev() {
        if (shutdown || !hasPrev()) return false;
        enter(currentIndex-1);
        return true;
    }

    public boolean gotoPhase(int newIndex) {
        if (shutdown || newIndex < 0 || newIndex >= phases.size() || newIndex == currentIndex) return false;
        enter(newIndex);
        return true;
    }

    public void shutdown() {
        if (shutdown) return;
        if (hasCurrent()) exit(currentIndex);
        shutdown = true;
    }

    private void enter(int newIndex) {
        if (shutdown) throw new IllegalStateException("ActiveTree already shutdown");
        if (newIndex < 0 || newIndex >= phases.size()) throw new IndexOutOfBoundsException();

        if (hasCurrent()) exit(currentIndex);
        currentIndex = newIndex;
        startPhase(currentIndex);
    }

    private void exit(int i) {
        Phase p = phases.get(i);
        try {
            p.stop();
        } catch (Exception e) {
            logger.warn("Phase exit failed: {}", p, e);
        }
    }

    private void startPhase(int i) {
        Phase p = phases.get(i);
        try {
            p.start(tree); // дефолтно — старт всех actions
        } catch (Exception e) {
            logger.warn("Phase start failed: {}", p, e);
            safeExitOnEnterFail(i);
            throw e;
        }
    }

    private void safeExitOnEnterFail(int i) {
        try { phases.get(i).stop(); } catch (Exception ignored) {}
        currentIndex = -1;
    }
}
