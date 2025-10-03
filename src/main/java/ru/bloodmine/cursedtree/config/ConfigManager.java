package ru.bloodmine.cursedtree.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.thedivazo.libs.dvzconfig.core.config.ConfigContainer;
import me.thedivazo.libs.dvzconfig.core.config.ConfigLoader;
import me.thedivazo.libs.dvzconfig.core.config.ConfigWrapper;
import me.thedivazo.libs.dvzconfig.yaml.YamlConfigLoader;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.nio.file.Path;

public class ConfigManager {

    private final ConfigContainer configContainer;

    @Inject
    public ConfigManager(@Named("pluginFolderPath") Path folderPath, TypeSerializer<ItemStack> itemStackSerializer, ScalarSerializer<TextComponent> textSerializer) {

        TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.builder()
                .register(TextComponent.class, textSerializer)
                .register(ItemStack.class, itemStackSerializer)
                .build();

        ConfigLoader configLoader = YamlConfigLoader.getConfigLoader(typeSerializerCollection); // from lang-eml module

        ConfigContainer configContainer = ConfigContainer.builder()
                .loader(configLoader)
                .addConfig(folderPath.resolve("config.yml"), MainConfig.class)
                .addConfig(folderPath.resolve("items.yml"), ItemConfig.class)
                .build();

        this.configContainer = configContainer;
    }

    public MainConfig getMainConfig() {
        return configContainer.getConfig(MainConfig.class);
    }

    public ItemConfig getItemConfig() {
        return configContainer.getConfig(ItemConfig.class);
    }

    public void saveItemConfig() {
        configContainer.getWrapper(ItemConfig.class).save();
    }

    public void reload() {
        configContainer.loadAll();
    }
}
