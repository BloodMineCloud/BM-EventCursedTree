package ru.bloodmine.cursedtree;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import ru.bloodmine.cursedtree.logger.PluginLoggerListener;
import ru.bloodmine.cursedtree.serializer.TextComponentSerializer;

import java.nio.file.Path;

public class BMCursedTreePlugin extends JavaPlugin {
    private Injector injector;

    @Inject
    private Logger log;

    public BMCursedTreePlugin() {
        super();
        this.injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindListener(Matchers.any(), new PluginLoggerListener(BMCursedTreePlugin.this.getSLF4JLogger()));
                        bind(new TypeLiteral<ScalarSerializer<TextComponent>>(){}).to(TextComponentSerializer.class).in(Scopes.SINGLETON);
                        bind(Path.class).annotatedWith(Names.named("pluginFolderPath")).toInstance(BMCursedTreePlugin.this.getDataFolder().toPath());
                    }
                },
                new ConfigModule()
        );
        getLogger().info("create Injector");

    }

    @Override
    public void onLoad() {
        injector.injectMembers(this);
        log.info("Loaded injector");
    }

    @Override
    public void onEnable() {
        log.info("Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        log.info("Plugin has been disabled");
    }
}
