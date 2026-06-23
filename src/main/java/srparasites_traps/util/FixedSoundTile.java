package srparasites_traps.util;

import cofh.core.audio.ISoundSource;
import cofh.core.audio.SoundTile;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Field;

// Fix the setFadeIn and setFadeOut setters because they use Math.min(0, x) which means that they will always set it to 0
public class FixedSoundTile extends SoundTile {
    public FixedSoundTile(ISoundSource source, SoundEvent sound, float volume, float pitch, boolean repeat, int repeatDelay, Vec3d pos) {
        super(source, sound, volume, pitch, repeat, repeatDelay, pos);
    }

    @Override
    public SoundTile setFadeIn(int fadeIn) {
        try {
            Field fadeInField = SoundTile.class.getDeclaredField("fadeIn");
            fadeInField.setAccessible(true);
            fadeInField.set(this, fadeIn);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return this;
        }

        return this;
    }

    @Override
    public SoundTile setFadeOut(int fadeOut) {
        try {
            Field fadeOutField = SoundTile.class.getDeclaredField("fadeOut");
            fadeOutField.setAccessible(true);
            fadeOutField.set(this, fadeOut);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return this;
        }

        return this;
    }
}
