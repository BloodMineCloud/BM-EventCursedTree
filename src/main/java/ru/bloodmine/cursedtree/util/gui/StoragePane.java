package ru.bloodmine.cursedtree.util.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ru.bloodmine.cursedtree.BMCursedTreePlugin;

import java.util.*;
import java.util.stream.Collectors;

public class StoragePane extends Pane {
    private final NamespacedKey key = new NamespacedKey(JavaPlugin.getProvidingPlugin(BMCursedTreePlugin.class), "IF-uuid");

    private final List<ItemStack> storage;
    private final List<GuiItem> view;
    @Getter
    private int currentPage = 0;

    protected StoragePane(List<ItemStack> items, @NotNull Slot slot, int length, int height, @NotNull Priority priority) {
        super(slot, length, height, priority);
        this.storage = items;
        this.view = items.stream().map(ItemStack::clone).map(GuiItem::new).collect(Collectors.toList());
    }

    public void add(ItemStack item) {
        storage.add(item);
        view.add(new GuiItem(item.clone()));
    }

    public void add(int index, ItemStack item) {
        storage.add(index, item);
        view.add(index, new GuiItem(item.clone()));
    }

    public ItemStack get(int index) {
        return storage.get(index);
    }

    public int getPages() {
        int total;
        if (view.isEmpty()) total = 1;
        else total = (view.size() / (length * height)) + 1;
        return total;
    }

    public void setPage(int page) {
        if (page < 0) return;
        currentPage = page;
    }

    @Override
    public void display(@NotNull InventoryComponent inventoryComponent, int paneOffsetX, int paneOffsetY, int maxLength, int maxHeight) {
        int baseX = getSlot().getX(maxLength) + paneOffsetX;
        int baseY = getSlot().getY(maxHeight) + paneOffsetY;

        int visibleW = Math.min(length, maxLength - baseX);   // сколько реально помещается по ширине
        int visibleH = Math.min(height, maxHeight - baseY);   // и по высоте

        for (int i = currentPage * visibleW * visibleH; i < view.size(); i++) {
            GuiItem gi = view.get(i);
            if (gi == null || !gi.isVisible()) continue;

            int lx = (i - currentPage * visibleW * visibleH) % length;
            int ly = (i - currentPage * visibleW * visibleH) / length;

            if (lx >= visibleW || ly >= visibleH) continue;

            inventoryComponent.setItem(gi, baseX + lx, baseY + ly);
        }
    }

    @Override
    public boolean click(@NotNull Gui gui, @NotNull InventoryComponent inventoryComponent, @NotNull InventoryClickEvent event, int slot, int paneOffsetX, int paneOffsetY, int maxLength, int maxHeight) {
        if (event.getClickedInventory() == null
                || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return false;
        }

        int invLen = inventoryComponent.getLength();
        int clickX = slot % invLen;
        int clickY = slot / invLen;

        int baseX = getSlot().getX(length) + paneOffsetX;
        int baseY = getSlot().getY(height) + paneOffsetY;
        int w = Math.min(length, maxLength);
        int h = Math.min(height, maxHeight);

        if (clickX < baseX || clickX >= baseX + w || clickY < baseY || clickY >= baseY + h) {
            return false;
        }

        int localX = clickX - baseX;
        int localY = clickY - baseY;
        int index = (localY * length + localX) + currentPage * w * h;

        event.setCancelled(true);

        if (index >= view.size()) return true;
        ItemStack storageItem = storage.get(index);

        // Пытаемся положить в инвентарь игрока (низ)
        Inventory bottom = event.getView().getBottomInventory();

        Map<Integer, ItemStack> leftover = bottom.addItem(storageItem);
        if (leftover.isEmpty()) {
            // всё ушло
            view.remove(index);
        } else {
            // вернулся остаток — оставим его в Storage
            ItemStack remainder = leftover.values().iterator().next();
            view.set(index, new GuiItem(remainder.clone()));
            storage.set(index, remainder);
        }

        gui.update();
        return true;
    }

    public void nextPage() {
        currentPage++;
        if (currentPage >= getPages()) currentPage = 0;
    }

    public void prevPage() {
        currentPage--;
        if (currentPage < 0) currentPage = getPages() - 1;
    }

    public @NotNull List<ItemStack> getItemStacks() {
        return Collections.unmodifiableList(storage);
    }

    @Override
    public @NotNull Collection<GuiItem> getItems() {
        return Collections.unmodifiableList(view);
    }

    @Override
    public @NotNull Collection<Pane> getPanes() {
        return List.of();
    }

    @Override
    public void clear() {
        storage.clear();
        view.clear();
    }
}
