package ru.bloodmine.cursedtree.util.gui;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bloodmine.cursedtree.config.ConfigManager;
import ru.bloodmine.cursedtree.logger.InjectLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@Singleton
public class EditConfigItemGui {

    private final static int STORAGE_HEIGHT = 5;
    private final static int CONTROL_HEIGHT = 1;
    private final static int LENGTH = 9;

    private final static int STORAGE_SIZE = LENGTH * STORAGE_HEIGHT;
    private final static int CONTROL_SIZE = LENGTH * CONTROL_HEIGHT;

    @InjectLogger
    private Logger logger;

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private ChestGui gui;
    private StoragePane storagePane;

    @Inject
    public EditConfigItemGui(JavaPlugin plugin, ConfigManager configManager) {
        this.plugin = plugin;
        this.configManager = configManager;

    }

    public void init() {
        if (gui != null) return;
        logger.info("Initializing GUI");
        gui = new ChestGui(
                6,
                ComponentHolder.of(Component.text(
                        "⭐ Редактор лута дерева ⭐"
                )),
                plugin
        );

        storagePane = new StoragePane(
                configManager.getItemConfig().getItems(),
                Slot.fromXY(0, 0),
                LENGTH,
                STORAGE_HEIGHT,
                Pane.Priority.HIGH
        );

        StaticPane controls = new StaticPane(
                Slot.fromXY(0, 5),
                LENGTH,
                CONTROL_HEIGHT
        );


        GuiItem clearAll = new GuiItem(head(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWQ5YmZlNWYyYmM1YTY0MDI0Zjc1OTFiMjRmOTUxMTI1MDhkN2Q2NmFkYzk5OGJmZDNjZTVhZmRmMTQ5YWU0ZiJ9fX0=",
                Component.text("Очистить всё").color(NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false)
        ), e -> {
            e.setCancelled(true);
            storagePane.clear();
            updatePageLabel(controls, storagePane.getCurrentPage(), storagePane.getPages());
            storagePane.setPage(1);
            gui.update();
        });
        GuiItem prev = new GuiItem(named(Material.ARROW, Component.text("⟨ Назад").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)), e -> {
            e.setCancelled(true);
            storagePane.prevPage();
            updatePageLabel(controls, storagePane.getCurrentPage(), storagePane.getPages());
            gui.update();
        });
        GuiItem next = new GuiItem(named(Material.ARROW, Component.text("Вперёд ⟩").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false)), e -> {
            e.setCancelled(true);
            storagePane.nextPage();
            updatePageLabel(controls, storagePane.getCurrentPage(), storagePane.getPages());
            gui.update();
        });

        controls.addItem(new GuiItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)), 0, 0);
        controls.addItem(new GuiItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)), 1, 0);
        controls.addItem(prev, 2, 0);
        updatePageLabel(controls, storagePane.getCurrentPage(), storagePane.getPages());
        controls.addItem(next, 4, 0);
        controls.addItem(new GuiItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)), 5, 0);
        controls.addItem(clearAll, 6, 0);
        controls.addItem(new GuiItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)), 7, 0);
        controls.addItem(new GuiItem(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)), 8, 0);

        gui.addPane(storagePane);
        gui.addPane(controls);

        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);
        });

        gui.setOnClose(event -> {
            HumanEntity humanEntity = event.getPlayer();
            humanEntity.sendMessage(
                    Component.text("Вы успешно сохранили вещи. Пожалуйста, перезагрузите плагин!")
                            .color(NamedTextColor.GOLD)
            );
            save();
        });
    }

    public void add(ItemStack stack) {
        if (gui == null) return;
        storagePane.add(stack);
        save();
        gui.update();
    }

    public void open(Player player) {
        if (gui == null) return;
        logger.info("Open tree loot inventory in {}", player.getName());
        gui.show(player);
    }

    public void save() {
        if (gui == null) return;
        configManager.getItemConfig().setItems(storagePane.getItemStacks());
        configManager.saveItemConfig();
    }

    public void update() {
        if (gui == null) return;
        storagePane.setItems(configManager.getItemConfig().getItems());
        gui.update();
    }

    public void shutdown() {
        if (gui == null) return;
        gui.getInventory().close();
        save();
        gui = null;
        storagePane = null;
    }

    // UTILS

    private static ItemStack head(String base64, Component name) {
        ItemStack it = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) it.getItemMeta();
        skullMeta.displayName(name);
        try {
            Method setProfile = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);

            GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            setProfile.invoke(skullMeta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LoggerFactory.getLogger(EditConfigItemGui.class).error("There was a severe internal reflection error when attempting to set the skin of a player skull via base64!", e);
        }
        it.setItemMeta(skullMeta);

        return it;
    }

    private static ItemStack named(Material material, Component name) {
        ItemStack it = new ItemStack(material);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            it.setItemMeta(meta);
        }
        return it;
    }

    private static void updatePageLabel(StaticPane controls, int page, int totalPages) {
        Component comp = Component.text()
                .append(Component.text("Страница ", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(page), NamedTextColor.WHITE))
                .append(Component.text("/", NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(totalPages), NamedTextColor.WHITE))
                .decoration(TextDecoration.ITALIC, false)
                .build();
        ItemStack label = named(Material.PAPER, comp);
        GuiItem guiItem = new GuiItem(label);
        guiItem.setAction(e -> e.setCancelled(true));
        controls.addItem(guiItem, 3, 0);
    }
}
