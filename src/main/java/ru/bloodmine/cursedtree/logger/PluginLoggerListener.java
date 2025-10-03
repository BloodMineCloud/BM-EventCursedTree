package ru.bloodmine.cursedtree.logger;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.slf4j.Logger;

import java.lang.reflect.Field;

public class PluginLoggerListener implements TypeListener {
    private final Logger logger;

    public PluginLoggerListener(Logger logger) {
        this.logger = logger;
    }

    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        Class<?> clazz = typeLiteral.getRawType();
        logger.info("Clazz " + clazz.getSimpleName() + " start logger injected...");
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.getType() == Logger.class && field.isAnnotationPresent(Inject.class) || field.isAnnotationPresent(jakarta.inject.Inject.class)) {
                    typeEncounter.register(new PluginLoggerInjector<T>(field, logger));
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
