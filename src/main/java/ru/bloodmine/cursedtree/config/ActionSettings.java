package ru.bloodmine.cursedtree.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@AllArgsConstructor
@Getter
public class ActionSettings {
    String name;
    String body;
}
