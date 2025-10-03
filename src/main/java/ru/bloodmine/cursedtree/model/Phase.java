package ru.bloodmine.cursedtree.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import net.kyori.adventure.text.Component;
import ru.bloodmine.cursedtree.model.action.Action;

import java.util.List;

@Builder
@Getter
public class Phase {
    private final Component phaseName;
    @Singular
    private List<Action> actions;
}
