package ru.bloodmine.cursedtree.model.parser;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.model.action.ItemDropAction;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDropParser implements ActionParser<ItemDropAction> {
    private static final Pattern PERIOD_FIELD = Pattern.compile("(?:p:|period:)?([0-9]+)");

    private final Logger logger;
    private final Iterable<ItemStack> droppedItems;
    private final Collection<BlockVector> droppedLocations;
    private final JavaPlugin plugin;

    public ItemDropParser(Logger logger, Iterable<ItemStack> droppedItems, Collection<BlockVector> droppedLocations, JavaPlugin plugin) {
        this.logger = logger;
        this.droppedItems = droppedItems;
        this.droppedLocations = droppedLocations;
        this.plugin = plugin;
    }

    @Override
    public boolean isValidSyntaxBody(String body) {
        return PERIOD_FIELD.matcher(body).matches();
    }

    @Override
    public ItemDropAction parse(String body) {
        Matcher periodMatcher = PERIOD_FIELD.matcher(body);
        if (!periodMatcher.matches()) throw new IllegalArgumentException("Invalid body '" + body +"'");

        int period = Integer.parseInt(periodMatcher.group(1));
        return new ItemDropAction(droppedItems, droppedLocations, period, plugin);
    }
}
