package ru.bloodmine.cursedtree.logger;

import com.google.inject.MembersInjector;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class PluginLoggerInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Logger logger;

    PluginLoggerInjector(Field field, Logger logger) {
        this.field = field;
        logger.info("Logger has been injected: " + field.getName());
        this.logger = logger;
        field.setAccessible(true);
    }

    public void injectMembers(T t) {
        try {
            field.set(t, logger);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
