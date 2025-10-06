package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.ProvidesIntoSet;
import ru.bloodmine.cursedtree.util.ItemInputManager;
import ru.bloodmine.cursedtree.util.PluginListener;

public class ListenerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ItemInputManager.class).in(Scopes.SINGLETON);
    }

    @ProvidesIntoSet
    public PluginListener itemInputManager(ItemInputManager itemInputManager) {
        return itemInputManager;
    }
}
