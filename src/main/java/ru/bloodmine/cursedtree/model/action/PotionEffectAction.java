package ru.bloodmine.cursedtree.model.action;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import ru.bloodmine.cursedtree.model.Tree;

@RequiredArgsConstructor
public class PotionEffectAction implements Action {
    private final PotionEffect effect;
    private final long period;
    private final double distance;
    private final JavaPlugin plugin;

    private BukkitTask bukkitTask;

    public void start(Tree tree) {
        if (bukkitTask != null) bukkitTask.cancel();
        bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getLocation().getWorld().equals(tree.spawnLocation().getWorld()))
                    .filter(player -> player.getLocation().distance(tree.spawnLocation()) <= distance)
                    .forEach(player -> player.addPotionEffect(effect));
        }, 0, period);
    }

    public void stop() {
        if (bukkitTask != null) bukkitTask.cancel();
        bukkitTask = null;
    }
}
