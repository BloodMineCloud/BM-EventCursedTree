package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.StringParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.model.Tree;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TpCommand extends AbstractCommand {

    private final static String TREE_ID_FIELD = "treeId";

    private final Map<String, Tree> trees;

    @Inject
    protected TpCommand(Logger logger, CommandManager<CommandSender> manager, @Named("trees") Map<String, Tree> trees) {
        super(logger, manager);
        this.trees = trees;
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder.
                literal("tp")
                .permission("cursedtree.tp")
                .argument(StringParser.<CommandSender>stringComponent()
                        .name(TREE_ID_FIELD)
                        .suggestionProvider((ctx, input) -> CompletableFuture.supplyAsync(() -> trees.values().stream()
                                .filter(tree -> tree.id().startsWith(input.readString().toLowerCase()))
                                .map(tree -> Suggestion.suggestion(tree.id()))
                                .toList()
                        ))
                        .required()
                        .build())
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        super.execute(commandContext);
        String treeId = commandContext.get(TREE_ID_FIELD);
        Tree tree = trees.get(treeId);
        if (tree == null) {
            commandContext.sender().sendMessage(Component.text("Неверный идентификатор дерева "+treeId, NamedTextColor.RED));
            return;
        }

        if (commandContext.sender() instanceof Entity entity) {
            entity.teleportAsync(tree.spawnLocation());
            entity.sendMessage(
                    Component.text("Вы успешно телепортированы к " + treeId, NamedTextColor.GREEN)
            );
        }
        else {
            commandContext.sender().sendMessage(
                    Component.text("Телепортация не удалась, вы не сущность")
                            .color(NamedTextColor.RED)
            );
        }
    }
}
