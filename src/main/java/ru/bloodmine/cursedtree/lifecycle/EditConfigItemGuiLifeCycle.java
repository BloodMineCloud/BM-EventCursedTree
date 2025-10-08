package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.util.gui.EditConfigItemGui;
import ru.bloodmine.cursedtree.util.LifeCyclable;

public class EditConfigItemGuiLifeCycle implements LifeCyclable {
    private final EditConfigItemGui manager;

    @Inject
    public EditConfigItemGuiLifeCycle(EditConfigItemGui manager) {
        this.manager = manager;
    }

    @Override
    public void load() {
        manager.init();
    }

    @Override
    public void shutdown() {
        manager.shutdown();
    }
}
