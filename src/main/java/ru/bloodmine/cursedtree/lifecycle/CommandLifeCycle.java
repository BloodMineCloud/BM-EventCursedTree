package ru.bloodmine.cursedtree.lifecycle;

import com.google.inject.Inject;
import ru.bloodmine.cursedtree.command.CommandPlugin;
import ru.bloodmine.cursedtree.util.LifeCyclable;

import java.util.Set;

public class CommandLifeCycle implements LifeCyclable {
    private final Set<CommandPlugin> commandPlugins;

    @Inject
    public CommandLifeCycle(Set<CommandPlugin> commandPlugins) {
        this.commandPlugins = commandPlugins;
    }

    @Override
    public void load() {
        for (CommandPlugin commandPlugin : commandPlugins) {
            commandPlugin.registerCommand();
        }
    }

    @Override
    public void shutdown() {

    }
}
