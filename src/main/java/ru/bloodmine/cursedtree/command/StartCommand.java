package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.service.ActivateTreeService;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class StartCommand extends AbstractCommand {

    private final static String TREE_ID_FIELD = "treeId";

    private final Map<String, Tree> trees;
    private final ActivateTreeService activateTreeService;

    @Inject
    public StartCommand(Logger logger, CommandManager<CommandSender> manager, @Named("trees") Map<String, Tree> trees, ActivateTreeService activateTreeService) {
        super(logger, manager);
        this.trees = Collections.unmodifiableMap(trees);
        this.activateTreeService = activateTreeService;
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("start")
                .permission("cursedtree.start")
                .argument(StringParser.<CommandSender>stringComponent()
                        .name(TREE_ID_FIELD)
                        .suggestionProvider((ctx, input) -> CompletableFuture.supplyAsync(() -> trees.values().stream()
                                .filter(tree -> tree.id().startsWith(input.readString().toLowerCase()))
                                .map(tree -> Suggestion.suggestion(tree.id()))
                                .toList()
                        ))
                        .optional()
                        .build()
                )
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        Optional<String> treeId = commandContext.optional(TREE_ID_FIELD);
        Tree startedTree = null;
        if (treeId.isPresent()) {
            Tree tree = trees.get(treeId.get());
            if (tree == null) {
                commandContext.sender().sendMessage(Component.text("Invalid tree ID "+treeId.get(), NamedTextColor.RED));
            }

            if (activateTreeService.startEvent(tree)) {
                startedTree = tree;
            }
        }
        else {
            startedTree = activateTreeService.startRandomEvent();
        }

        if (startedTree != null) {
            commandContext.sender().sendMessage(
                    Component.text("Дерево '"+startedTree.id()+"' запущено. Координаты ", NamedTextColor.GREEN)
                            .append(
                                    Component.text(startedTree.spawnLocation().getX() +" " + startedTree.spawnLocation().getY() + " " + startedTree.spawnLocation().getZ()).clickEvent(
                                            ClickEvent.runCommand("/minecraft:tp "+startedTree.spawnLocation().getX() +" " + startedTree.spawnLocation().getY() + " " + startedTree.spawnLocation().getZ())
                                    ).color(NamedTextColor.RED).append(Component.text(" (Кликабельно)").color(NamedTextColor.GRAY))
                            )
            );
        }
        else {
            commandContext.sender().sendMessage(Component.text("Tree is not started", NamedTextColor.RED));
        }
    }
}
