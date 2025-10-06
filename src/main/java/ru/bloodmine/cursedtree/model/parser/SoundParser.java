package ru.bloodmine.cursedtree.model.parser;

import org.bukkit.Sound;
import ru.bloodmine.cursedtree.model.action.Action;
import ru.bloodmine.cursedtree.model.action.SoundAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoundParser implements ActionParser<SoundAction> {
    private static final Pattern TYPE_FIELD = Pattern.compile("(?:t:|type:)([a-zA-Z_]+)");

    private static final Pattern VOLUME_FIELD = Pattern.compile("(?:v:|volume:)([0-9]*\\.?[0-9]+([eE][0-9]+)?)");
    private static final Pattern PITCH_FIELD = Pattern.compile("(?:p:|pitch:)([0-9]*\\.?[0-9]+([eE][0-9]+)?)");

    @Override
    public String name() {
        return "sound";
    }

    @Override
    public boolean isValidSyntaxBody(String body) {
        return TYPE_FIELD.matcher(body).matches();
    }

    @Override
    public SoundAction parse(String body) {
        Matcher typeMatcher = TYPE_FIELD.matcher(body);
        if (!typeMatcher.matches()) throw new IllegalArgumentException("No found of 'type' field from body '" + body +"'");
        Sound sound = Sound.valueOf(typeMatcher.group(1));

        float volume = 10F;
        Matcher volumeMatcher = VOLUME_FIELD.matcher(body);
        if (volumeMatcher.find()) volume = Float.parseFloat(volumeMatcher.group(1));

        float pitch = 1F;
        Matcher pitchMatcher = PITCH_FIELD.matcher(body);
        if (pitchMatcher.find()) pitch = Float.parseFloat(pitchMatcher.group(1));

        return new SoundAction(sound, volume, pitch);
    }
}
