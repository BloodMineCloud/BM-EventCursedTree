package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import me.clip.placeholderapi.PlaceholderAPI;
import ru.bloodmine.cursedtree.hook.PlaceholderHook;
import ru.bloodmine.cursedtree.util.LifeCyclable;

public class PlaceholderHookLifeCycle implements LifeCyclable {

    private final PlaceholderHook placeholderHook;

    @Inject
    public PlaceholderHookLifeCycle(PlaceholderHook placeholderHook) {
        this.placeholderHook = placeholderHook;
    }

    @Override
    public void load() {
        placeholderHook.register();
    }

    @Override
    public void shutdown() {
        placeholderHook.unregister();
    }
}
