package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import ru.bloodmine.cursedtree.hook.PlaceholderHook;

public class PlaceholderHookModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PlaceholderHook.class).in(Scopes.SINGLETON);
    }
}
