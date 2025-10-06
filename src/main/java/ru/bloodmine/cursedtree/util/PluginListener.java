package ru.bloodmine.cursedtree.util;

import org.bukkit.event.Listener;

public interface PluginListener extends Listener {
    void register();
    void unregister();
}
