package ru.bloodmine.cursedtree.config;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import me.thedivazo.libs.dvzconfig.core.config.ConfigContainer;
import me.thedivazo.libs.dvzconfig.core.config.ConfigLoader;
import me.thedivazo.libs.dvzconfig.core.config.ConfigWrapper;
import me.thedivazo.libs.dvzconfig.yaml.YamlConfigLoader;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.spongepowered.configurate.serialize.ScalarSerializer;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import ru.bloodmine.cursedtree.serializer.BlockVectorSerializer;

import java.nio.file.Path;
import java.util.Map;

public class ConfigManager {

    private final ConfigContainer configContainer;

    @Inject
    public ConfigManager(
            @Named("pluginFolderPath") Path folderPath,
            TypeSerializer<ActionSettings> actionSettingsSerializer,
            TypeSerializer<ItemStack> itemStackSerializer,
            TypeSerializer<TextComponent> textSerializer,
            TypeSerializer<BlockVector> blockVectorSerializer,
            TypeSerializer<Location> locationSerializer
    ) {

        TypeSerializerCollection typeSerializerCollection = TypeSerializerCollection.builder()
                .register(ActionSettings.class, actionSettingsSerializer)
                .register(TextComponent.class, textSerializer)
                .register(ItemStack.class, itemStackSerializer)
                .register(BlockVector.class, blockVectorSerializer)
                .register(Location.class, locationSerializer)
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
