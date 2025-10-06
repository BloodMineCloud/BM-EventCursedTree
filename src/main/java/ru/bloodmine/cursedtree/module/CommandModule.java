package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.multibindings.ProvidesIntoSet;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import ru.bloodmine.cursedtree.command.*;

public class CommandModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(PhaseCommand.class).in(Scopes.SINGLETON);
        bind(ReloadCommand.class).in(Scopes.SINGLETON);
        bind(StartCommand.class).in(Scopes.SINGLETON);
        bind(StopCommand.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public CommandManager<CommandSender> commandManager(JavaPlugin plugin) {
        LegacyPaperCommandManager<CommandSender> commandManager = LegacyPaperCommandManager.createNative(
                plugin,
                ExecutionCoordinator.simpleCoordinator()
        );
        commandManager.registerAsynchronousCompletions();
        return commandManager;
    }

    @ProvidesIntoSet
    public CommandPlugin phaseCommand(PhaseCommand command) {
        return command;
    }

    @ProvidesIntoSet
    public CommandPlugin reloadCommand(ReloadCommand command) {
        return command;
    }

    @ProvidesIntoSet
    public CommandPlugin startCommand(StartCommand command) {
        return command;
    }

    @ProvidesIntoSet
    public CommandPlugin stopCommand(StopCommand command) {
        return command;
    }
}
