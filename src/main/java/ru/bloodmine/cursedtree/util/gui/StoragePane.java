package ru.bloodmine.cursedtree.util.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.InventoryComponent;
import com.github.stefvanschie.inventoryframework.gui.type.util.Gui;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoragePane extends Pane {

    private final List<GuiItem> storage;
    private int currentPage = 0;

    protected StoragePane(List<ItemStack> items, @NotNull Slot slot, int length, int height, @NotNull Priority priority) {
        super(slot, length, height, priority);
        storage = items.stream().map(GuiItem::new).collect(Collectors.toList());
    }

    public void setItems(List<ItemStack> items) {
        storage.clear();
        storage.addAll(items.stream().map(GuiItem::new).toList());
    }

    public void add(ItemStack item) {
        storage.add(new GuiItem(item));
    }

    public void add(int index, ItemStack item) {
        storage.add(index, new GuiItem(item));
    }

    public ItemStack get(int index) {
        return storage.get(index).getItem();
    }

    public int getPages() {
        int total;
        if (storage.isEmpty()) total = 1;
        else total = (storage.size() / (length * height)) + 1;
        return Math.max(currentPage + 1, total);
    }

    public void setPage(int page) {
        if (page < 1) return;
        currentPage = page-1;
    }

    public int getCurrentPage() {
        return currentPage+1;
    }

    @Override
    public void display(@NotNull InventoryComponent inventoryComponent, int paneOffsetX, int paneOffsetY, int maxLength, int maxHeight) {
        int baseX = getSlot().getX(maxLength) + paneOffsetX;
        int baseY = getSlot().getY(maxHeight) + paneOffsetY;

        int visibleW = Math.min(length, maxLength - baseX);   // сколько реально помещается по ширине
        int visibleH = Math.min(height, maxHeight - baseY);   // и по высоте

        for (int i = currentPage * visibleW * visibleH; i < storage.size(); i++) {
            GuiItem gi = storage.get(i);
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

        if (index >= storage.size()) return true;
        GuiItem stack = storage.get(index);

        // Пытаемся положить в инвентарь игрока (низ)
        Inventory bottom = event.getView().getBottomInventory();
        ItemStack toMove = stack.getItem().clone();

        Map<Integer, ItemStack> leftover = bottom.addItem(toMove);
        if (leftover.isEmpty()) {
            // всё ушло
            storage.remove(index);
        } else {
            // вернулся остаток — оставим его в Storage
            storage.set(index, new GuiItem(leftover.values().iterator().next()));
        }

        gui.update();
        return true;
    }

    public void nextPage() {
        currentPage++;
    }

    public void prevPage() {
        if (currentPage == 0) return;
        currentPage--;
    }

    public @NotNull List<ItemStack> getItemStacks() {
        return storage.stream().map(GuiItem::getItem).collect(Collectors.toList());
    }

    @Override
    public @NotNull Collection<GuiItem> getItems() {
        return Collections.unmodifiableList(storage);
    }

    @Override
    public @NotNull Collection<Pane> getPanes() {
        return List.of();
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
