package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.BMCursedTreePlugin;

public class ReloadService {
    private final BMCursedTreePlugin plugin;

    @Inject
    public ReloadService(BMCursedTreePlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        plugin.reload();
    }
}
