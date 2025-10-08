package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.util.LifeCyclable;

import java.util.Set;

public class LifeCycleService {
    private final Set<LifeCyclable> lifeCyclables;

    @Inject
    private Logger logger;

    @Inject
    public LifeCycleService(Set<LifeCyclable> lifeCyclables) {
        this.lifeCyclables = lifeCyclables;
    }

    public void load() {
        lifeCyclables.forEach(lifeCyclable -> {
            lifeCyclable.load();
            logger.info("Loaded lifeCyclable {}", lifeCyclable.getClass().getSimpleName());
        });
    }

    public void shutdown() {
        lifeCyclables.forEach(LifeCyclable::shutdown);
    }
}
