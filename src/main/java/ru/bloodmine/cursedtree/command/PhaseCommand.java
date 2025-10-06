package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.model.ActiveTree;
import ru.bloodmine.cursedtree.service.ActivateTreeService;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class PhaseCommand extends AbstractCommand {
    private static final String ACTION_NAME = "action";

    private final ActivateTreeService activateTreeService;

    @Inject
    public PhaseCommand(Logger logger, CommandManager<CommandSender> manager, ActivateTreeService activateTreeService) {
        super(logger, manager);
        this.activateTreeService = activateTreeService;
    }

    public enum Action {
        NEXT,
        PREV
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("phase")
                .permission("cursedtree.phase")
                .argument(
                        EnumParser.<CommandSender, Action>enumComponent(Action.class)
                                .name(ACTION_NAME)
                                .suggestionProvider((ctx, input) -> CompletableFuture.supplyAsync(() -> Arrays.stream(Action.values())
                                                .filter(action -> action.name().toLowerCase().startsWith(input.readString().toLowerCase()))
                                                .map(action -> Suggestion.suggestion(action.name().toLowerCase()))
                                                .toList())
                                )
                                .required()
                                .build()
                )
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        ActiveTree activeTree = activateTreeService.getCurrentActiveTree();
        CommandSender sender = commandContext.sender();
        Action action = commandContext.get(ACTION_NAME);

        final Component PREFIX = Component.text("CursedTree » ").color(NamedTextColor.DARK_AQUA);

        if (activeTree == null) {
            sender.sendMessage(PREFIX.append(
                    Component.text("Активного дерева нет.", NamedTextColor.RED)
            ));
            return;
        }

        if (action == Action.NEXT) {
            if (!activeTree.hasNext()) {
                sender.sendMessage(PREFIX.append(
                        Component.text("Уже на последней фазе.", NamedTextColor.YELLOW)
                ));
                activeTree.currentPhaseName().ifPresent(name ->
                        sender.sendMessage(PREFIX.append(Component.text("Текущая фаза: ", NamedTextColor.GRAY)).append(name))
                );
                return;
            }

            activeTree.next();
            sender.sendMessage(PREFIX.append(Component.text("Перешли к следующей фазе: ", NamedTextColor.GREEN))
                    .append(activeTree.currentPhaseName().orElse(Component.text("<без имени>", NamedTextColor.GRAY))));
        }
        else if (action == Action.PREV) {
            if (!activeTree.hasPrev()) {
                sender.sendMessage(PREFIX.append(
                        Component.text("Уже на первой фазе.", NamedTextColor.YELLOW)
                ));
                activeTree.currentPhaseName().ifPresent(name ->
                        sender.sendMessage(PREFIX.append(Component.text("Текущая фаза: ", NamedTextColor.GRAY)).append(name))
                );
                return;
            }

            activeTree.prev();
            sender.sendMessage(PREFIX.append(Component.text("Вернулись к предыдущей фазе: ", NamedTextColor.GREEN))
                    .append(activeTree.currentPhaseName().orElse(Component.text("<без имени>", NamedTextColor.GRAY))));
        }
        else {
            sender.sendMessage(Component.text("Неизвестное действие. Используйте: next или prev", NamedTextColor.RED));
        }
    }
}
