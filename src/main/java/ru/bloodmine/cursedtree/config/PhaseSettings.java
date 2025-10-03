package ru.bloodmine.cursedtree.config;

import net.kyori.adventure.text.Component;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
public  class PhaseSettings {
    private Component phaseName = Component.text("Phase");
    private List<ActionSettings> actions = new ArrayList<>();
}
