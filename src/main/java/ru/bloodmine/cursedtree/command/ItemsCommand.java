package ru.bloodmine.cursedtree.command;

import com.google.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.context.CommandContext;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.util.ItemInputManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ItemsCommand extends AbstractCommand {

    private final ConfigManager configManager;
    private final ItemInputManager itemInputManager;

    @Inject
    public ItemsCommand(Logger logger, CommandManager<CommandSender> manager, ConfigManager configManager, ItemInputManager itemInputManager) {
        super(logger, manager);
        this.configManager = configManager;
        this.itemInputManager = itemInputManager;
    }

    @Override
    protected Command<CommandSender> buildCommand(CommandManager<CommandSender> manager, Command.Builder<CommandSender> builder) {
        return builder
                .literal("items")
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

        CompletableFuture<List<ItemStack>> completableFuture = itemInputManager.readItems(player, configManager.getItemConfig().getItems());
        completableFuture.thenAccept(items -> {
            player.sendMessage(
                    Component.text("Вы успешно сохранили вещи. Пожалуйста, перезагрузите плагин!")
                    .color(NamedTextColor.GOLD)
            );
            configManager.getItemConfig().setItems(items);
            configManager.saveItemConfig();
        });

        player.sendMessage(
                Component.text("Положите или заберите из инвентаря вещи, чтобы отредактировать лут дерева")
                        .color(NamedTextColor.GREEN)
        );
    }
}
