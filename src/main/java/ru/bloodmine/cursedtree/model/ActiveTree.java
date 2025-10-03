package ru.bloodmine.cursedtree.model;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;
import ru.bloodmine.cursedtree.model.action.Action;

import java.util.List;

public class ActiveTree {
    private final Tree activeTree;
    private final List<Phase> phases;
    private int currentPhaseIndex = -1;

    public ActiveTree(Tree activeTree, List<Phase> phases) {
        this.phases = phases;
        this.activeTree = activeTree;
    }

    public boolean isValidPhaseIndex() {
        return currentPhaseIndex >= phases.size() || currentPhaseIndex <= 0;
    }

    public boolean isFirstPhase() {
        return currentPhaseIndex == 0;
    }

    public boolean isLastPhase() {
        return currentPhaseIndex == phases.size() - 1;
    }

    @Nullable
    public Component getCurrentPhaseName() {
        if (isValidPhaseIndex()) return null;
        return phases.get(currentPhaseIndex).getPhaseName();
    }

    public boolean nextPhase() {
        if (isValidPhaseIndex()) return false;

        int prevIndex = currentPhaseIndex;

        if (prevIndex >= 0) {
            for (Action action : phases.get(currentPhaseIndex).getActions()) {
                action.stop();
            }
        }

        int nextIndex = currentPhaseIndex + 1;

        if (nextIndex < phases.size()) {
            for (Action action : phases.get(nextIndex).getActions()) {
                action.start(activeTree);
            }
        }

        currentPhaseIndex = nextIndex;
        return true;
    }

    public boolean prevPhase() {
        if (isValidPhaseIndex()) return false;

        int prevIndex = currentPhaseIndex;

        if (prevIndex < phases.size()) {
            for (Action action : phases.get(currentPhaseIndex).getActions()) {
                action.stop();
            }
        }

        int nextIndex = currentPhaseIndex - 1;

        if (nextIndex >= 0) {
            for (Action action : phases.get(nextIndex).getActions()) {
                action.start(activeTree);
            }
        }

        currentPhaseIndex = prevIndex;
        return true;
    }

    public void shutdown() {
        if (!isValidPhaseIndex()) return;
        for (Action action : phases.get(currentPhaseIndex).getActions()) {
            action.stop();
        }
    }


}
