package ru.bloodmine.cursedtree.model.parser;

import ru.bloodmine.cursedtree.model.action.Action;

public interface ActionParser<A extends Action> {
    String name();

    boolean isValidSyntaxBody(String body);
    A parse(String body);
}
