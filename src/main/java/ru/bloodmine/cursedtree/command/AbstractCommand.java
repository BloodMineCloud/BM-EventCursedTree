package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bloodmine.cursedtree.logger.InjectLogger;

public abstract class AbstractCommand implements CommandExecutionHandler<CommandSender>, CommandPlugin {

    protected final Logger logger;

    private final CommandManager<CommandSender> manager;

    private boolean shutdown = false;

    protected AbstractCommand(Logger logger, CommandManager<CommandSender> manager) {
        this.logger = logger;
        this.manager = manager;
    }

    protected abstract Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder);

    @Override
    public void registerCommand() {
        logger.info("Registering command {}", this.getClass().getSimpleName());
        manager.command(
                buildCommand(manager, manager.commandBuilder("cursedtree").handler(this))
        );
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        if (shutdown) throw new IllegalStateException("Command is already shutdown");
    }

    @Override
    public void shutdown() {
        shutdown = true;
    }
}
