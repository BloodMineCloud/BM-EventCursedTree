package ru.bloodmine.cursedtree.model.parser;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockVector;
import org.slf4j.Logger;
import ru.bloodmine.cursedtree.logger.InjectLogger;
import ru.bloodmine.cursedtree.model.action.ItemDropAction;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemDropParser implements ActionParser<ItemDropAction> {
    private static final Pattern PERIOD_FIELD = Pattern.compile("(?:p:|period:)?([0-9]+)");
    private static final Pattern RANDOM_LOC_FIELD = Pattern.compile("(?:r:|random:)?(true|false)");
    private static final Pattern PLAYER_RADIUS_FIELD = Pattern.compile("(?:p:|radius:)?([0-9]+)");

    @InjectLogger
    private Logger logger;

    private final Iterable<ItemStack> droppedItems;
    private final Collection<BlockVector> droppedLocations;
    private final JavaPlugin plugin;

    @Inject
    public ItemDropParser(@Named("droppedItems") List<ItemStack> droppedItems, @Named("droppedLocations") List<BlockVector> droppedLocations, JavaPlugin plugin) {
        this.droppedItems = droppedItems;
        this.droppedLocations = droppedLocations;
        this.plugin = plugin;
    }

    @Override
    public String name() {
        return "itemdrop";
    }

    @Override
    public boolean isValidSyntaxBody(String body) {
        return PERIOD_FIELD.matcher(body).find();
    }

    @Override
    public ItemDropAction parse(String body) {
        Matcher periodMatcher = PERIOD_FIELD.matcher(body);
        if (!periodMatcher.find()) throw new IllegalArgumentException("Invalid body '" + body +"'");
        int period = Integer.parseInt(periodMatcher.group(1));

        boolean random = true;
        Matcher randomLocMatcher = RANDOM_LOC_FIELD.matcher(body);
        if (randomLocMatcher.find()) {
            random = Boolean.parseBoolean(randomLocMatcher.group(1));
        }

        double radius = 50;
        Matcher playerRadiusMatcher = PLAYER_RADIUS_FIELD.matcher(body);
        if (playerRadiusMatcher.find()) {
            radius = Double.parseDouble(playerRadiusMatcher.group(1));
        }
        return new ItemDropAction(logger, droppedItems, droppedLocations, period, random, radius, plugin);
    }
}
