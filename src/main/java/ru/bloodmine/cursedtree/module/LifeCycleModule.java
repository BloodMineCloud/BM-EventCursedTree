package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.multibindings.ProvidesIntoSet;
import ru.bloodmine.cursedtree.lifecycle.*;
import ru.bloodmine.cursedtree.service.LifeCycleService;
import ru.bloodmine.cursedtree.util.LifeCyclable;

public class LifeCycleModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(LifeCycleService.class).in(Scopes.SINGLETON);

        bind(CommandLifeCycle.class).in(Scopes.SINGLETON);
        bind(TreeLifeCycle.class).in(Scopes.SINGLETON);
        bind(EditConfigItemGuiLifeCycle.class).in(Scopes.SINGLETON);
        //bind(ListenerLifeCycle.class).in(Scopes.SINGLETON);
        bind(PlaceholderHookLifeCycle.class).in(Scopes.SINGLETON);
    }

    @ProvidesIntoSet
    public LifeCyclable lifecycle(CommandLifeCycle lifeCycle) { return lifeCycle; }

    @ProvidesIntoSet
    public LifeCyclable lifecycle(TreeLifeCycle lifeCycle) {
        return lifeCycle;
    }

    @ProvidesIntoSet
    public LifeCyclable lifecycle(EditConfigItemGuiLifeCycle lifeCycle) {
        return lifeCycle;
    }

//    @ProvidesIntoSet
//    public LifeCyclable lifecycle(ListenerLifeCycle lifeCycle) {
//        return lifeCycle;
//    }

    @ProvidesIntoSet
    public LifeCyclable lifecycle(PlaceholderHookLifeCycle lifeCycle) {
        return lifeCycle;
    }
}
