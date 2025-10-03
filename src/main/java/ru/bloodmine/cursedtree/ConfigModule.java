package ru.bloodmine.cursedtree;

import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.serializer.ItemStackSerializer;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<TypeSerializer<ItemStack>>(){}).to(ItemStackSerializer.class).in(Scopes.SINGLETON);
    }

    @Provides
    public @Named("schematicFolderPath") Path schematicFolderPath(ConfigManager configManager) {
        return configManager.getMainConfig().getSchematicFolderPath();
    }

    @Provides
    public @Named("schematicTree") String schematicTree(ConfigManager configManager) {
        return configManager.getMainConfig().getSchematicTree();
    }

    @Provides
    public @Named("cubeRadius") double cubeRadius(ConfigManager configManager) {
        return configManager.getMainConfig().getRegion().getCubeRadius();
    }

    @Provides
    public @Named("flags") Map<Flag<?>, Object> flags(ConfigManager configManager) {
        return configManager.getMainConfig().getRegion().getFlags().entrySet().stream()
                .collect(Collectors.toMap(entry ->
                        Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), entry.getKey()), Map.Entry::getValue)
                );
    }
}
