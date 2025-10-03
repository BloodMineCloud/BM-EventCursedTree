package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import org.bukkit.util.BlockVector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
@Getter
public class MainConfig {
    private Path schematicFolderPath;
    private String schematicTree;
    private RegionSettings region = new RegionSettings();
    private Map<String, PhaseSettings> phases = new HashMap<>();
    private List<BlockVector> droppedLocations = new ArrayList<>();
    private Map<String, PositionSettings> saplings = new HashMap<>();
}
