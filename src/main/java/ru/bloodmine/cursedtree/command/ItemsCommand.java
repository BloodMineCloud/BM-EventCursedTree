package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.standard.EnumParser;
import org.incendo.cloud.suggestion.Suggestion;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.util.gui.EditConfigItemGui;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ItemsCommand extends AbstractCommand {
    private static final String ACTION_NAME = "action";

    private final ConfigManager configManager;
    private final EditConfigItemGui itemInputManager;

    @Inject
    public ItemsCommand(Logger logger, CommandManager<CommandSender> manager, ConfigManager configManager, EditConfigItemGui itemInputManager) {
        super(logger, manager);
        this.configManager = configManager;
        this.itemInputManager = itemInputManager;
    }

    public enum Action {
        OPEN,
        ADD
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("items")
                .argument(EnumParser.<CommandSender, Action>enumComponent(Action.class)
                        .name(ACTION_NAME)
                        .suggestionProvider((ctx, input) -> CompletableFuture.supplyAsync(() -> Arrays.stream(Action.values())
                                .filter(action -> action.name().toLowerCase().startsWith(input.readString().toLowerCase()))
                                .map(action -> Suggestion.suggestion(action.name().toLowerCase()))
                                .toList())
                        )
                        .optional()
                        .build())
                .permission("cursedtree.items")
                .build();
    }

    @Override
    public void execute(@NonNull CommandContext<CommandSender> commandContext) {
        super.execute(commandContext);
        if (!(commandContext.sender() instanceof Player player)) {
            commandContext.sender().sendMessage(
                    Component.text("Данную команду может выполнить только игрок!")
                            .color(NamedTextColor.RED)
            );
            return;
        }
        Action action = commandContext.getOrDefault(ACTION_NAME, Action.OPEN);
        if (action == Action.ADD) {
            ItemStack itemStack = player.getEquipment() == null ? null : player.getEquipment().getItemInMainHand();
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                player.sendMessage(Component.text("Вы должны держать предмет в руке, который хотите добавить").color(NamedTextColor.RED));
            }
            else {
                itemInputManager.add(itemStack);
                player.sendMessage(Component.text("Предмет ").color(NamedTextColor.GREEN).append(itemStack.displayName().append(Component.text(" X "+itemStack.getAmount()))).append(Component.text(" успешно добавлен в дроп").color(NamedTextColor.GREEN)));
                player.sendMessage(Component.text("Перезагрузите плагин чтобы применить изменения").color(NamedTextColor.GOLD));
                player.getEquipment().setItemInMainHand(null);
            }
        }
        else if (action == Action.OPEN) {

            itemInputManager.open(player);

            player.sendMessage(
                    Component.text("Положите или заберите из инвентаря вещи, чтобы отредактировать лут дерева")
                            .color(NamedTextColor.GREEN)
            );
        }
    }
}
