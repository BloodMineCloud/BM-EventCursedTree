package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.BlockVector;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigSerializable
@Getter
public class MainConfig {
    private Path schematicFolderPath = Paths.get("example/path");
    private String schematicDefault = "schematic";
    private RegionSettings region = new RegionSettings();
    private List<PhaseSettings> phases = new ArrayList<>();
    private List<BlockVector> droppedLocations = new ArrayList<>();
    private Map<String, Location> saplings = new HashMap<>();
}
