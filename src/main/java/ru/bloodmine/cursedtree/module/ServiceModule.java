package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import ru.bloodmine.cursedtree.service.ActivateTreeService;
import ru.bloodmine.cursedtree.service.RegionService;
import ru.bloodmine.cursedtree.service.SchematicActorFactory;
import ru.bloodmine.cursedtree.service.TreePlacerService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SchematicActorFactory.class).in(Scopes.SINGLETON);
        bind(RegionService.class).in(Scopes.SINGLETON);
        bind(TreePlacerService.class).in(Scopes.SINGLETON);
        bind(ActivateTreeService.class).in(Scopes.SINGLETON);
    }
}
