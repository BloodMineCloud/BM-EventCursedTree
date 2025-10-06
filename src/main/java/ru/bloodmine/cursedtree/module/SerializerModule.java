package ru.bloodmine.cursedtree.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.spongepowered.configurate.serialize.TypeSerializer;
import ru.bloodmine.cursedtree.config.ActionSettings;
import ru.bloodmine.cursedtree.serializer.*;

public class SerializerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<TypeSerializer<ActionSettings>>(){}).to(ActionSerializer.class).in(Scopes.SINGLETON);
        bind(new TypeLiteral<TypeSerializer<ItemStack>>(){}).to(ItemStackSerializer.class).in(Scopes.SINGLETON);
        bind(new TypeLiteral<TypeSerializer<Location>>(){}).to(LocationSerializer.class).in(Scopes.SINGLETON);
        bind(new TypeLiteral<TypeSerializer<TextComponent>>(){}).to(TextComponentSerializer.class).in(Scopes.SINGLETON);
        bind(new TypeLiteral<TypeSerializer<BlockVector>>(){}).to(BlockVectorSerializer.class).in(Scopes.SINGLETON);
    }
}
