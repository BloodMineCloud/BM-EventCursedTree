package ru.bloodmine.cursedtree.model.action;

import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import ru.bloodmine.cursedtree.model.Tree;

@RequiredArgsConstructor
public class SoundAction implements Action {
    private final Sound sound;
    private final float volume;
    private final float pitch;

    @Override
    public void start(Tree tree) {
        tree.spawnLocation().getWorld().playSound(tree.spawnLocation(), sound, volume, pitch);
    }
}
