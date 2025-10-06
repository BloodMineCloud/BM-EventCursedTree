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
import ru.bloodmine.cursedtree.service.ActivateTreeService;

public class StopCommand extends AbstractCommand {
    private final ActivateTreeService activateTreeService;

    @Inject
    public StopCommand(Logger logger, CommandManager<CommandSender> manager, ActivateTreeService activateTreeService) {
        super(logger, manager);
        this.activateTreeService = activateTreeService;
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("stop")
                .permission("cursedtree.stop")
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        boolean isStopped = activateTreeService.endCurrentEvent();
        if (isStopped) {
            commandContext.sender().sendMessage(Component.text("Дерево успешно остановлено").color(NamedTextColor.GREEN));
        }
        else {
            commandContext.sender().sendMessage(Component.text("Ивент уже остановлен или не запущен").color(NamedTextColor.YELLOW));
        }
    }
}
