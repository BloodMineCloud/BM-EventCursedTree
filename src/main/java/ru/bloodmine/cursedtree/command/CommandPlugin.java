package ru.bloodmine.cursedtree.command;

public interface CommandPlugin {
    void registerCommand();

    void shutdown();
}
