package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@Getter
public  class PhaseSettings {
    private String phaseName = "Phase";
    private List<ActionSettings> actions = new ArrayList<>();
}
