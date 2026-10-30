package com.example.verity;

import com.example.verity.block.VerityBoxBlock;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Блоки мода. */
public final class VerityBlocks {
    public static final String SEAT_TAG = "verity_seat";

    /** Активные "сиденья" в коробках (для уборки, когда Verity вылез). */
    public static final Set<ArmorStandEntity> SEATS = ConcurrentHashMap.newKeySet();

    public static final Block VERITY_BOX = new VerityBoxBlock(
        FabricBlockSettings.create().strength(0.5f).sounds(BlockSoundGroup.WOOD).nonOpaque());

    public static void register() {
        Identifier id = new Identifier(VerityMod.MOD_ID, "verity_box");
        Registry.register(Registries.BLOCK, id, VERITY_BOX);
        Registry.register(Registries.ITEM, id, new BlockItem(VERITY_BOX, new FabricItemSettings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.add(VERITY_BOX));

        // Убираем сиденье, когда на нём никто не сидит
        ServerTickEvents.END_SERVER_TICK.register(server -> SEATS.removeIf(seat -> {
            if (seat.isRemoved()) return true;
            if (seat.getPassengerList().isEmpty()) {
                seat.discard();
                return true;
            }
            return false;
        }));

        // Сиденья, оставшиеся в мире после перезапуска, удаляем
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ArmorStandEntity stand
                && stand.getCommandTags().contains(SEAT_TAG)
                && !SEATS.contains(stand)) {
                world.getServer().execute(stand::discard);
            }
        });
    }

    private VerityBlocks() {}
}
