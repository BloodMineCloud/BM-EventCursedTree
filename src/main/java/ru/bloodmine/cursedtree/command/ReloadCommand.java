package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.service.ReloadService;

public class ReloadCommand extends AbstractCommand{
    private final ReloadService service;

    @Inject
    public ReloadCommand(Logger logger, CommandManager<CommandSender> commandManager, ReloadService lifeCycleManager) {
        super(logger, commandManager);
        this.service = lifeCycleManager;
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("reload")
                .permission("cursedtree.reload")
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        commandContext.sender().sendMessage(Component.text("Plugin reloading...", NamedTextColor.GREEN));
        try {
            service.reload();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            commandContext.sender().sendMessage(Component.text("Ошибка перезагрузки! Подробнее в консоли: " + e.getMessage(), NamedTextColor.RED));
        }

        commandContext.sender().sendMessage(Component.text("Plugin reloaded!", NamedTextColor.GREEN));
    }
}
