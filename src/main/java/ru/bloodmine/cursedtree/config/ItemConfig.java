package ru.bloodmine.cursedtree.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.ArrayList;
import java.util.List;

@ConfigSerializable
@Getter
@Setter
public class ItemConfig {
    private List<ItemStack> items = new ArrayList<>(List.of(
            new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 16),
            new ItemStack(Material.GREEN_DYE, 16),
            new ItemStack(Material.ORANGE_STAINED_GLASS, 21)
    ));
}
