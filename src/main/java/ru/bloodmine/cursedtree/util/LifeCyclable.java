package ru.bloodmine.cursedtree.util;

public interface LifeCyclable {
    default void load() {};
    default void shutdown() {};
}
