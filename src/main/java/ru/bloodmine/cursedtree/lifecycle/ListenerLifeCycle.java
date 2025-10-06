package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.util.LifeCyclable;
import ru.bloodmine.cursedtree.util.PluginListener;

import java.util.Set;

public class ListenerLifeCycle implements LifeCyclable {
    private final Set<PluginListener> listeners;

    @Inject
    public ListenerLifeCycle(Set<PluginListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void load() {
        for (PluginListener listener : listeners) {
            listener.register();
        }
    }

    @Override
    public void shutdown() {
        for (PluginListener listener : listeners) {
            listener.unregister();
        }
    }
}
