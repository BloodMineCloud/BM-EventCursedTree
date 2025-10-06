package ru.bloodmine.cursedtree;

import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.logger.PluginLoggerListener;
import ru.bloodmine.cursedtree.module.*;
import ru.bloodmine.cursedtree.serializer.TextComponentSerializer;
import ru.bloodmine.cursedtree.service.LifeCycleService;
import ru.bloodmine.cursedtree.service.ReloadService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

public class BMCursedTreePlugin extends JavaPlugin {
    private Injector configInjector;
    private Injector serviceInjector;

    @Override
    public void onLoad() {
        try {
            this.configInjector = Guice.createInjector(
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bindListener(Matchers.any(), new PluginLoggerListener(BMCursedTreePlugin.this.getSLF4JLogger()));
                            bind(new TypeLiteral<ScalarSerializer<TextComponent>>() {}).to(TextComponentSerializer.class).in(Scopes.SINGLETON);
                            bind(Path.class).annotatedWith(Names.named("pluginFolderPath")).toInstance(BMCursedTreePlugin.this.getDataFolder().toPath());
                            bind(ReloadService.class).toInstance(new ReloadService(BMCursedTreePlugin.this));
                            bind(JavaPlugin.class).toInstance(BMCursedTreePlugin.this);
                            bind(Logger.class).toInstance(BMCursedTreePlugin.this.getSLF4JLogger());
                            bind(ConfigManager.class).in(Scopes.SINGLETON);
                        }
                    },
                    new SerializerModule()
            );
            getLogger().info("create MainInjector");
            getLogger().info("Loaded injector");
        } catch (Exception e) {
            getSLF4JLogger().error(e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void reload() {

        ConfigManager configManager = configInjector.getInstance(ConfigManager.class);
        configManager.reload();
        getLogger().info("Config load");

        if (serviceInjector != null) {
            LifeCycleService lifeCycleService = serviceInjector.getInstance(LifeCycleService.class);
            lifeCycleService.shutdown();
        }

        serviceInjector = configInjector.createChildInjector(
                new ConfigModule(getSLF4JLogger()),
                new ServiceModule(),
                new LifeCycleModule(),
                new CommandModule(),
                new ListenerModule(),
                new PlaceholderHookModule()
        );

        LifeCycleService lifeCycleService = serviceInjector.getInstance(LifeCycleService.class);
        lifeCycleService.load();

        getLogger().info("New service injector created and load");
    }

    @Override
    public void onEnable() {
        try {
            reload();
            getLogger().info("Plugin has been enabled");
        } catch (Exception e) {
            getSLF4JLogger().error(e.getMessage(), e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (serviceInjector != null) {
            LifeCycleService lifeCycleService = serviceInjector.getInstance(LifeCycleService.class);
            lifeCycleService.shutdown();
        }
        getLogger().info("Plugin has been disabled");
    }

    @SuppressWarnings("unchecked")
    public static void unregisterAllPluginCommands(JavaPlugin plugin) {
        final SimpleCommandMap commandMap = (SimpleCommandMap) Bukkit.getServer().getCommandMap();

        try {
            final Field f = SimpleCommandMap.class.getDeclaredField("knownCommands");
            f.setAccessible(true);
            final Map<String, Command> known = (Map<String, Command>) f.get(commandMap);

            final Iterator<Map.Entry<String, Command>> it = known.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry<String, Command> e = it.next();
                final Command cmd = e.getValue();

                if (cmd instanceof PluginIdentifiableCommand pic && pic.getPlugin() == plugin) {
                    cmd.unregister(commandMap);
                    it.remove();
                }
            }
        } catch (ReflectiveOperationException ex) {
            plugin.getLogger().severe("Failed to unregister commands: " + ex.getMessage());
        }

        // Обновить таб-комплит/Brigadier у клиентов и консоли
        try {
            Method method = Server.class.getDeclaredMethod("syncCommands");
            method.invoke(Bukkit.getServer());
        } catch (Throwable ignored) {
            // на всякий: если нет (кастомные ядра), просто молча пропускаем
        }
    }
}
