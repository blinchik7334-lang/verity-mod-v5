package com.example.verity;

import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;

/** Музыкальная пластинка (конструктор у MusicDiscItem защищённый, поэтому нужен наследник). */
public class VerityDiscItem extends MusicDiscItem {
    public VerityDiscItem(int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
    }
}
