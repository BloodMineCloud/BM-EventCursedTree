package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.util.LifeCyclable;

import java.util.Set;

public class LifeCycleService {
    private final Set<LifeCyclable> lifeCyclables;

    @Inject
    public LifeCycleService(Set<LifeCyclable> lifeCyclables) {
        this.lifeCyclables = lifeCyclables;
    }

    public void load() {
        lifeCyclables.forEach(LifeCyclable::load);
    }

    public void shutdown() {
        lifeCyclables.forEach(LifeCyclable::shutdown);
    }
}
