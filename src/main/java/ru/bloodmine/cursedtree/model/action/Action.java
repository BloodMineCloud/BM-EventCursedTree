package ru.bloodmine.cursedtree.model.action;

import ru.bloodmine.cursedtree.model.Tree;

public interface Action {
    void start(Tree tree);
    default void stop() {};
}
