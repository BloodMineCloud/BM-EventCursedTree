package ru.bloodmine.cursedtree.service;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SchematicActor {
    private final Logger logger;
    private final JavaPlugin plugin;
    private final Path schematicDir;
    private final Map<String, Clipboard> loadedSchematics = new HashMap<>();
    private final Map<String, EditSession> activeSchematics = new HashMap<>();

    public SchematicActor(Logger logger, JavaPlugin plugin, Path schematicFolderPath) {
        this.logger = logger;
        this.plugin = plugin;
        this.schematicDir = schematicFolderPath;
    }

    public boolean loadSchematic(String schematicName) {
        File file = schematicDir.resolve(schematicName).toFile();
        if (!file.exists()) {
            logger.warn("Schematic {} does not exist", schematicName);
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            plugin.getLogger().warning("Unknown schematic format: " + schematicName);
            return false;
        }

        try (FileInputStream fis = new FileInputStream(file);
             ClipboardReader reader = format.getReader(fis)) {
            Clipboard clipboard = reader.read();
            loadedSchematics.put(schematicName, clipboard);
            return true;
        } catch (IOException e) {
            logger.error("Failed to load schematic: {}", schematicName, e);
            return false;
        }
    }

    public void paste(String schematicName, String sessionName, Location location) {
        if (!loadedSchematics.containsKey(schematicName) && !loadSchematic(schematicName)) {
            return;
        }

        Clipboard clipboard = loadedSchematics.get(schematicName);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(location.getWorld());
        BlockVector3 position = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        EditSession prevSession = activeSchematics.get(sessionName);

        if (prevSession != null) {
            try (EditSession undoSession = WorldEdit.getInstance().newEditSession(prevSession.getWorld())) {
                prevSession.undo(undoSession);
            }
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            ClipboardHolder holder = new ClipboardHolder(clipboard);

            Operation operation = holder.createPaste(editSession)
                    .to(position)
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(operation);
            activeSchematics.put(sessionName, editSession);
        } catch (WorldEditException e) {
            //Откат в случае ошибки
            if (prevSession != null) {
                try (EditSession redoSession = WorldEdit.getInstance().newEditSession(prevSession.getWorld())) {
                    prevSession.redo(redoSession);
                }
            }
            throw new RuntimeException(e);
        }
    }
    public void undoSchematic(String sessionName) {
        EditSession originalSession = activeSchematics.get(sessionName);
        if (originalSession == null) {
            return;
        }

        try (EditSession undoSession = WorldEdit.getInstance().newEditSession(originalSession.getWorld())) {
            originalSession.undo(undoSession);
            activeSchematics.remove(sessionName);
        }
    }

    public boolean schemaExist(String schematicName) {
        File file = schematicDir.resolve(schematicName).toFile();
        if (!file.exists()) return false;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        return format != null;
    }

    public boolean isActive(String sessionName) {
        return activeSchematics.containsKey(sessionName);
    }
}
