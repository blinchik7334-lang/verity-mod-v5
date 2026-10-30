package com.example.verity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class VerityEntities {
    public static final EntityType<VerityEntity> VERITY = Registry.register(
        Registries.ENTITY_TYPE, new Identifier(VerityMod.MOD_ID, "verity"),
        EntityType.Builder.create(VerityEntity::new, SpawnGroup.CREATURE)
            .setDimensions(0.9f, 1.2f).maxTrackingRange(8).build("verity:verity"));

    public static final SpawnEggItem VERITY_SPAWN_EGG = Registry.register(
        Registries.ITEM, new Identifier(VerityMod.MOD_ID, "verity_spawn_egg"),
        new SpawnEggItem(VERITY, 0xD7B7E8, 0x34243F, new net.minecraft.item.Item.Settings()));

    public static void register() {
        FabricDefaultAttributeRegistry.register(VERITY, VerityEntity.createAttributes());
    }
    private VerityEntities() {}
}
