package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@Getter
public class ItemConfig {
    private List<ItemStack> items;
}
