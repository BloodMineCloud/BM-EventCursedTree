package ru.bloodmine.cursedtree.model.action;

import com.google.common.collect.Iterators;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import ru.bloodmine.cursedtree.model.Tree;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class ItemDropAction implements Action {
    private final Iterable<ItemStack> droppedItems;
    private final Collection<BlockVector> droppedLocations;
    private final long period;
    private final JavaPlugin plugin;

    private BukkitTask bukkitTask;

    @Override
    public void start(Tree tree) {
        if (bukkitTask != null) bukkitTask.cancel();
        Iterator<Location> locationIterator = Iterators.cycle(
                droppedLocations.stream()
                        .map(vector -> tree.spawnLocation().add(vector))
                        .toList()
        );
        Iterator<ItemStack> dropItemIterator = droppedItems.iterator();
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (dropItemIterator.hasNext() && locationIterator.hasNext()) {
                ItemStack droppedItem = dropItemIterator.next();
                Location location = locationIterator.next();
                location.getWorld().dropItemNaturally(location, droppedItem);
            }
            else {
                stop();
            }
        }, 0, period);
    }

    @Override
    public void stop() {
        if (bukkitTask != null) bukkitTask.cancel();
        bukkitTask = null;
    }
}
