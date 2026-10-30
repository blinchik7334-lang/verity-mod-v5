package com.example.verity;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

/** Пластинки Horror Skunx: It's me, It's verity, Lord Verity. */
public final class VerityDiscs {

    // id пластинки -> длительность песни в секундах.
    // !!! ПОМЕНЯЙ числа на реальную длину своих песен (в секундах): 3 минуты 20 секунд = 200 !!!
    private static final String[] IDS      = {"its_me", "its_verity", "lord_verity"};
    private static final int[]    SECONDS  = { 180,      180,          180 };
    private static final int[]    COMPARATOR = { 13,     14,           15 };

    public static final Map<String, Item> DISCS = new LinkedHashMap<>();

    public static void register() {
        for (int i = 0; i < IDS.length; i++) {
            Identifier soundId = new Identifier(VerityMod.MOD_ID, "record." + IDS[i]);
            SoundEvent sound = Registry.register(Registries.SOUND_EVENT, soundId, SoundEvent.of(soundId));

            Item disc = new VerityDiscItem(COMPARATOR[i], sound,
                new FabricItemSettings().maxCount(1).rarity(Rarity.RARE), SECONDS[i]);
            Registry.register(Registries.ITEM, new Identifier(VerityMod.MOD_ID, "disc_" + IDS[i]), disc);
            DISCS.put(IDS[i], disc);
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            for (Item disc : DISCS.values()) {
                entries.add(disc);
            }
        });
    }

    private VerityDiscs() {}
}
