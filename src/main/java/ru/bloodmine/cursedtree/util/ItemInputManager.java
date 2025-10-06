package ru.bloodmine.cursedtree.util;

import com.google.inject.Inject;
import com.sk89q.worldguard.session.Session;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ItemInputManager implements PluginListener {

    private final JavaPlugin plugin;

    @Inject
    public ItemInputManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public CompletableFuture<List<ItemStack>> readItems(Player player, List<ItemStack> existedItems) {
        if (sessions.containsKey(player.getUniqueId())) {
            Session session = sessions.remove(player.getUniqueId());
            if (player.getOpenInventory() == session.inventory) {
                session.completeOnce();
            }
        }

        CompletableFuture<List<ItemStack>> future = new CompletableFuture<>();

        Runnable task = () -> {
            int size = 54;
            Inventory inventory = Bukkit.createInventory(null, size, Component.text(">> Редактор лута <<").color(NamedTextColor.GOLD));

            int idx = 0;
            for (ItemStack is : existedItems) {
                if (is == null || is.getType() == Material.AIR) continue;
                if (idx >= size) break;
                inventory.setItem(idx++, is.clone()); // не трогаем исходные объекты
            }

            sessions.put(player.getUniqueId(), new Session(player.getUniqueId(), inventory, future));

            player.openInventory(inventory);
        };

        Bukkit.getScheduler().runTask(plugin, task);

        return future;
    }

    public void closeAllSessions() {
        for (Session s : new ArrayList<>(sessions.values())) {
            Player p = Bukkit.getPlayer(s.playerId);
            sessions.remove(s.playerId);
            if (p != null) {
                p.getOpenInventory();
                if (p.getOpenInventory().getTopInventory() == s.inventory) {
                    p.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player player)) return;
        Session s = sessions.get(player.getUniqueId());
        if (s == null) return;
        if (e.getInventory() != s.inventory) return;
        s.completeOnce();
        sessions.remove(player.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        handlePlayerGone(e.getPlayer());
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        handlePlayerGone(e.getPlayer());
    }

    private void handlePlayerGone(Player player) {
        Session s = sessions.remove(player.getUniqueId());
        if (s == null) return;
        s.completeOnce();
        sessions.remove(player.getUniqueId());
    }

    @Override
    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void unregister() {
        InventoryCloseEvent.getHandlerList().unregister(this);
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerKickEvent.getHandlerList().unregister(this);
    }

    private static final class Session {
        final UUID playerId;
        final Inventory inventory;
        final CompletableFuture<List<ItemStack>> future;
        private boolean done = false;

        Session(UUID playerId, Inventory inventory, CompletableFuture<List<ItemStack>> future) {
            this.playerId = playerId;
            this.inventory = inventory;
            this.future = future;
        }

        synchronized void completeOnce() {
            if (done) return;
            done = true;
            List<ItemStack> list = new ArrayList<>(inventory.getSize());
            for (ItemStack itemStack : inventory) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                list.add(itemStack.clone());
            }
            future.complete(list);
        }
    }

    private static int nextMultipleOf9(int n) {
        return ((n + 8) / 9) * 9;
    }

}
