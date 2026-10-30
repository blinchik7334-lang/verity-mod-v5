package com.example.verity.block;

import com.example.verity.VerityBlocks;
import com.example.verity.VerityState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

/** Картонная коробка с открытым верхом. ПКМ — Verity садится внутрь (Shift — вылезти). */
public class VerityBoxBlock extends Block {

    // Дно 2px + четыре стенки по 2px, высота 12px (открытый верх)
    private static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(0, 0, 0, 16, 2, 16),
        Block.createCuboidShape(0, 2, 0, 16, 12, 2),
        Block.createCuboidShape(0, 2, 14, 16, 12, 16),
        Block.createCuboidShape(0, 2, 2, 2, 12, 14),
        Block.createCuboidShape(14, 2, 2, 16, 12, 14));

    public VerityBoxBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
                              Hand hand, BlockHitResult hit) {
        if (world.isClient) return ActionResult.SUCCESS;
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;

        // Садиться могут только игроки, превращённые в Verity
        if (!VerityState.SERVER.containsKey(player.getUuid())) {
            player.sendMessage(Text.literal("§7Сесть в коробку может только Verity."), true);
            return ActionResult.CONSUME;
        }
        if (player.hasVehicle()) return ActionResult.CONSUME;

        // Место уже занято?
        List<ArmorStandEntity> seats = world.getEntitiesByClass(ArmorStandEntity.class, new Box(pos),
            e -> e.getCommandTags().contains(VerityBlocks.SEAT_TAG));
        if (!seats.isEmpty()) {
            player.sendMessage(Text.literal("§7В коробке уже кто-то сидит."), true);
            return ActionResult.CONSUME;
        }

        // Невидимое "сиденье" — маркер-стойка, на которую садится игрок
        ArmorStandEntity seat = EntityType.ARMOR_STAND.create(world);
        if (seat == null) return ActionResult.CONSUME;
        seat.refreshPositionAndAngles(pos.getX() + 0.5, pos.getY() + 0.475, pos.getZ() + 0.5, player.getYaw(), 0.0f);

        NbtCompound nbt = new NbtCompound();
        seat.writeNbt(nbt);
        nbt.putBoolean("Marker", true);
        nbt.putBoolean("Invisible", true);
        nbt.putBoolean("NoGravity", true);
        nbt.putBoolean("Invulnerable", true);
        seat.readNbt(nbt);
        seat.addCommandTag(VerityBlocks.SEAT_TAG);

        VerityBlocks.SEATS.add(seat);
        world.spawnEntity(seat);
        player.startRiding(seat, true);
        player.sendMessage(Text.literal("§dТы сидишь в коробке. Shift — вылезти."), true);
        return ActionResult.CONSUME;
    }
}
