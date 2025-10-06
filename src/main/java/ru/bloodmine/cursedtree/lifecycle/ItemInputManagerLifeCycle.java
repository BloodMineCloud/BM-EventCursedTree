package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.util.ItemInputManager;
import ru.bloodmine.cursedtree.util.LifeCyclable;

public class ItemInputManagerLifeCycle implements LifeCyclable {
    private final ItemInputManager manager;

    @Inject
    public ItemInputManagerLifeCycle(ItemInputManager manager) {
        this.manager = manager;
    }

    @Override
    public void shutdown() {
        manager.closeAllSessions();
    }
}
