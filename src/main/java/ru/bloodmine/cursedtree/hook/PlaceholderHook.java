package ru.bloodmine.cursedtree.hook;

import com.google.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.bloodmine.cursedtree.model.Tree;
import ru.bloodmine.cursedtree.service.ActivateTreeService;

import java.time.Duration;
import java.time.LocalTime;

public class PlaceholderHook extends PlaceholderExpansion {
    private static final String PHASE_PLACEHOLDER = "active_tree_phase";

    private static final String X_PLACEHOLDER = "active_tree_x";
    private static final String Y_PLACEHOLDER = "active_tree_y";
    private static final String Z_PLACEHOLDER = "active_tree_z";
    private static final String WORLD_PLACEHOLDER = "active_tree_world";

    private final ActivateTreeService activateTreeService;

    @Inject
    public PlaceholderHook(ActivateTreeService activateTreeService) {
        this.activateTreeService = activateTreeService;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "cursedtree";
    }

    @Override
    public @NotNull String getAuthor() {
        return "BloodMine&TheDiVaZo";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (activateTreeService.getCurrentActiveTree() == null) return null;

        if (params.equalsIgnoreCase(PHASE_PLACEHOLDER)) {
            return activateTreeService.getCurrentActiveTree().currentPhaseName().orElse("null");
        }

        Tree activeTree = activateTreeService.getCurrentActiveTree().getTree();

        if (params.equalsIgnoreCase(X_PLACEHOLDER)) {
            return String.valueOf(activeTree.x());
        }
        else if (params.equalsIgnoreCase(Y_PLACEHOLDER)) {
            return String.valueOf(activeTree.y());
        }
        else if (params.equalsIgnoreCase(Z_PLACEHOLDER)) {
            return String.valueOf(activeTree.z());
        }
        else if (params.equalsIgnoreCase(WORLD_PLACEHOLDER)) {
            return activeTree.spawnLocation().getWorld().getName();
        }

        return null;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        return onRequest(player, params);
    }
}
