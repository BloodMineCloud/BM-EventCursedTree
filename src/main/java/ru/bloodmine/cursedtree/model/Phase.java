package ru.bloodmine.cursedtree.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.kyori.adventure.text.Component;
import ru.bloodmine.cursedtree.model.action.Action;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class Phase {
    private final Component phaseName;
    @Singular
    private List<Action> actions;

    public void stop() {
        for (Action action : actions) {
            action.stop();
        }
    }

    public void start(Tree tree) {
        for (Action action : actions) {
            action.start(tree);
        }
    }
}
