package ru.bloodmine.cursedtree.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;

import java.io.File;
import java.nio.file.Path;

public class SchematicActorFactory {

    @InjectLogger
    private Logger logger;
    private final JavaPlugin plugin;
    private final Path schematicDir;

    @Inject
    public SchematicActorFactory(JavaPlugin plugin, @Named("schematicFolderPath") Path schematicFolderPath) {
        this.plugin = plugin;
        this.schematicDir = schematicFolderPath;
    }

    public SchematicActor createSchematicActor() {
        return new SchematicActor(logger, plugin, schematicDir);
    }

    public boolean schemExist(String schematicName) {
        File file = schematicDir.resolve(schematicName).toFile();
        if (!file.exists()) return false;

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) return false;

        return true;
    }
}
