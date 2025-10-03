package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
@Getter
public class RegionSettings {
    private int cubeRadius = 20;
    private Map<String, Object> flags = new HashMap<>();
}
