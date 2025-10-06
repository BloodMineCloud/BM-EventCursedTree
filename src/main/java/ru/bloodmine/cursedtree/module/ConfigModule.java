package ru.bloodmine.cursedtree.module;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bloodmine.cursedtree.config.ActionSettings;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.config.PhaseSettings;
import ru.bloodmine.cursedtree.model.Phase;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.model.action.*;
import ru.bloodmine.cursedtree.model.parser.*;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigModule extends AbstractModule {

    private final Logger logger;

    public ConfigModule(Logger logger) {
        this.logger = logger;
    }

    @Override
    protected void configure() {
        Multibinder<ActionParser<?>> parsers =
                Multibinder.newSetBinder(binder(), new TypeLiteral<>() {
                });

        parsers.addBinding().to(ItemDropParser.class).in(Scopes.SINGLETON);
        parsers.addBinding().to(PotionEffectParser.class).in(Scopes.SINGLETON);
        parsers.addBinding().to(SchemParser.class).in(Scopes.SINGLETON);
        parsers.addBinding().to(SoundParser.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public Map<String, ActionParser<?>> mapParsers(Set<ActionParser<?>> parsers) {
        return parsers.stream()
                .map(parser -> Map.entry(parser.name(), parser))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // CONFIG VARIABLES

    @Provides
    @Singleton
    public @Named("schematicFolderPath") Path schematicFolderPath(@Named("pluginFolderPath") Path pluginFolderPath, ConfigManager configManager) {
        return configManager.getMainConfig().getSchematicFolderPath();
    }

    @Provides
    @Singleton
    public @Named("schematicDefault") String schematicDefault(ConfigManager configManager) {
        return configManager.getMainConfig().getSchematicDefault();
    }

    @Provides
    @Singleton
    public @Named("cubeRadius") double cubeRadius(ConfigManager configManager) {
        return configManager.getMainConfig().getRegion().getCubeRadius();
    }

    @Provides
    @Singleton
    public @Named("flags") Map<Flag<?>, Object> flags(ConfigManager configManager) {
        Map<Flag<?>, Object> flagMap = new HashMap<>();
        for (Map.Entry<String, String> flagEntry : configManager.getMainConfig().getRegion().getFlags().entrySet()) {
             Flag<?> flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), flagEntry.getKey());
            try {
                Object value = flag.parseInput(FlagContext.create().setInput(flagEntry.getValue()).build());
                flagMap.put(flag, value);
            } catch (InvalidFlagFormat e) {
                logger.warn("Invalid flag value -> ", e);
            }
        }

        return flagMap;
    }

    @Provides
    @Singleton
    public @Named("trees") Set<Tree> trees(@Named("trees") Map<String, Tree> trees) {
        return new HashSet<>(trees.values());
    }

    @Provides
    @Singleton
    public @Named("trees") Map<String, Tree> treeMap(ConfigManager configManager) {
        return configManager.getMainConfig().getSaplings().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey() ,new Tree(entry.getKey(), entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Provides
    @Singleton
    public @Named("phases") List<Phase> phases(ConfigManager configManager, Map<String, ActionParser<?>> mapActionParsers) {
        List<PhaseSettings> phaseSettings = configManager.getMainConfig().getPhases();
        List<Phase> phases = new ArrayList<>();
        for (PhaseSettings phaseSetting : phaseSettings) {
            List<Action> actions = new ArrayList<>();
            for (ActionSettings actionSetting : phaseSetting.getActions()) {
                ActionParser<?> actionParser = mapActionParsers.get(actionSetting.getName().toLowerCase());
                if (actionParser == null) {
                    logger.warn("Unknown action parser '{}'", actionSetting.getName());
                }
                else {
                    actions.add(actionParser.parse(actionSetting.getBody()));
                }
            }
            phases.add(new Phase(phaseSetting.getPhaseName(), actions));
        }

        return phases;
    }

    @Provides
    public @Named("droppedItems") List<ItemStack> droppedItems(ConfigManager configManager) {
        return configManager.getItemConfig().getItems();
    }

    @Provides
    public @Named("droppedLocations") List<BlockVector> droppedLocations(ConfigManager configManager) {
        return configManager.getMainConfig().getDroppedLocations().stream().map(BlockVector::clone).toList();
    }
}
