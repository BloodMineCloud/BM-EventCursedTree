package ru.bloodmine.cursedtree.model.action;

import com.google.common.collect.Iterators;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;
import ru.bloodmine.cursedtree.model.Tree;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class ItemDropAction implements Action {
    private static final int END_TICK = 120;

    @InjectLogger
    private final Logger logger;
    private final Iterable<ItemStack> droppedItems;
    private final Collection<BlockVector> droppedVectors;
    private final long period;
    private final boolean randomLoc;
    private final double playerRadius;
    private final JavaPlugin plugin;

    private BukkitTask bukkitTask;

    private final Set<BukkitRunnable> bukkitTasks = ConcurrentHashMap.newKeySet();

    @Override
    public void start(Tree tree) {
        if (bukkitTask != null) bukkitTask.cancel();

        List<Location> droppedLocations = droppedVectors.stream()
                .map(vector -> tree.spawnLocation().clone().add(vector))
                .collect(ArrayList::new, List::add, List::addAll);
        if (randomLoc) {
            Collections.shuffle(droppedLocations);
        }

        Iterator<Location> locationIterator = Iterators.cycle(droppedLocations);
        Iterator<ItemStack> dropItemIterator = droppedItems.iterator();
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            boolean hasPlayer = tree.spawnLocation().getNearbyPlayers(playerRadius, playerRadius, 10).stream().findAny().isPresent();
            if ( hasPlayer && dropItemIterator.hasNext() && locationIterator.hasNext()) {
                ItemStack droppedItem = dropItemIterator.next();
                Location location = locationIterator.next();
                Item item = location.getWorld().dropItemNaturally(location, droppedItem);

                item.setVelocity(new Vector(0, -0.05, 0));
                item.setGlowing(true);

                AtomicInteger counter = new AtomicInteger(0);

                final int effectPeriod = 2;

                BukkitRunnable bukkitRunnable = new BukkitRunnable() {
                    @Override
                    public void run() {
                        int i = counter.getAndAdd(effectPeriod);
                        if (i >= END_TICK || item.isOnGround()) {
                            cancel();
                            return;
                        }
                        item.getWorld().spawnParticle(
                                Particle.SOUL_FIRE_FLAME,
                                item.getLocation().add(0, 0.25, 0),
                                2,
                                0.1, 0.1, 0.1,
                                0
                        );
                    }
                };

                bukkitRunnable.runTaskTimer(plugin, 0 , effectPeriod);
                logger.info("Dropped Item {} in location {}", droppedItem.getType(), location);
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
        bukkitTasks.forEach(BukkitRunnable::cancel);
        bukkitTasks.clear();
    }
}
