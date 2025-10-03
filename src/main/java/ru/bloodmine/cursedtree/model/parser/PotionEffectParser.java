package ru.bloodmine.cursedtree.model.parser;

import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.bloodmine.cursedtree.model.action.Action;
import ru.bloodmine.cursedtree.model.action.PotionEffectAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class PotionEffectParser implements ActionParser<PotionEffectAction> {
    private static final Pattern TYPE_FIELD = Pattern.compile("(?:t:|type:)([a-zA-Z_]+)");
    private static final Pattern DURATION_FIELD = Pattern.compile("(?:du:|duration:)([0-9]+)");
    private static final Pattern AMPLIFIER_FIELD = Pattern.compile("(?:amp:|amplifier:)([0-9]+)");
    private static final Pattern AMBIENT_FIELD = Pattern.compile("(?:amb:|ambient:)(true|false)");
    private static final Pattern PARTICLES_FIELD = Pattern.compile("(?:pa:|particles:)(true|false)");
    private static final Pattern ICON_FIELD = Pattern.compile("(?:i:|icon:)(true|false)");

    private static final Pattern PERIOD_FIELD = Pattern.compile("(?:pe:|period:)([0-9]+)");
    private static final Pattern DISTANCE_FIELD = Pattern.compile("(?:di:|distance:)([0-9]*\\.?[0-9]+([eE][0-9]+)?)");

    private final JavaPlugin plugin;

    @Override
    public boolean isValidSyntaxBody(String body) {
        return TYPE_FIELD.matcher(body).find() &&
                DURATION_FIELD.matcher(body).find() &&
                AMBIENT_FIELD.matcher(body).find() &&
                PERIOD_FIELD.matcher(body).find() &&
                DISTANCE_FIELD.matcher(body).find();
    }

    @Override
    public PotionEffectAction parse(String body) {
        Matcher typeMatcher = TYPE_FIELD.matcher(body);
        if (!typeMatcher.find()) throw new IllegalArgumentException("No found of 'type' field from body '" + body +"'");
        Matcher durationMatcher = DURATION_FIELD.matcher(body);
        if (!durationMatcher.find()) throw new IllegalArgumentException("No found of 'duration' field from body '" + body +"'");
        Matcher amplifierMatcher = AMPLIFIER_FIELD.matcher(body);
        if (!amplifierMatcher.find()) throw new IllegalArgumentException("No found of 'amplifier' field from body '" + body +"'");

        Matcher periodMatcher = PERIOD_FIELD.matcher(body);
        if (!periodMatcher.find()) throw new IllegalArgumentException("No found of 'period' field from body '" + body +"'");
        Matcher distanceMatcher = DISTANCE_FIELD.matcher(body);
        if (!distanceMatcher.find()) throw new IllegalArgumentException("No found of 'distance' field from body '" + body +"'");

        String type = typeMatcher.group(1);
        int duration = Integer.parseInt(durationMatcher.group(1));
        int amplifier = Integer.parseInt(amplifierMatcher.group(1));
        long period = Long.parseLong(periodMatcher.group(1));
        double distance = Double.parseDouble(distanceMatcher.group(1));

        boolean ambient = true;
        boolean particles = true;
        boolean icon = true;

        Matcher ambientMatcher = AMBIENT_FIELD.matcher(body);
        if (ambientMatcher.find()) {
            ambient = Boolean.parseBoolean(ambientMatcher.group(1));
        }

        Matcher particlesMatcher = PARTICLES_FIELD.matcher(body);
        if (particlesMatcher.find()) {
            particles = Boolean.parseBoolean(particlesMatcher.group(1));
        }

        Matcher iconMatcher = ICON_FIELD.matcher(body);
        if (iconMatcher.find()) {
            icon = Boolean.parseBoolean(iconMatcher.group(1));
        }
        PotionEffectType potionEffectType = PotionEffectType.getByName(type);

        if (potionEffectType == null) throw new IllegalArgumentException("No such potion effect type '" + type + "'");

        PotionEffect potionEffect = new PotionEffect(
                potionEffectType,
                duration,
                amplifier,
                ambient,
                particles,
                icon
        );
        return new PotionEffectAction(potionEffect, period, distance, plugin);
    }
}
