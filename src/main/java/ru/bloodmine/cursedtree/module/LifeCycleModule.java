package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.ProvidesIntoSet;
import ru.bloodmine.cursedtree.lifecycle.CommandLifeCycle;
import ru.bloodmine.cursedtree.service.ActivateTreeService;
import ru.bloodmine.cursedtree.lifecycle.TreeLifeCycle;
import ru.bloodmine.cursedtree.service.LifeCycleService;
import ru.bloodmine.cursedtree.util.LifeCyclable;

public class LifeCycleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LifeCycleService.class).in(Scopes.SINGLETON);
    }

    @ProvidesIntoSet
    public LifeCyclable activateTreeService(CommandLifeCycle lifeCycle) {
        return lifeCycle;
    }

    @ProvidesIntoSet
    public LifeCyclable activateTreeService(TreeLifeCycle lifeCycle) {
        return lifeCycle;
    }
}
