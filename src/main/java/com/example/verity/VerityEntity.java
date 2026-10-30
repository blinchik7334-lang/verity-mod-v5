package com.example.verity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import java.util.EnumSet;

public class VerityEntity extends PathAwareEntity {
    public VerityEntity(EntityType<? extends PathAwareEntity> type, World world) { super(type, world); }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
            .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
            .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.24)
            .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 20.0);
    }

    @Override protected void initGoals() {
        // Verity notices a nearby player and walks over to them, but does not attack.
        goalSelector.add(1, new ApproachPlayerGoal(this, 0.85, 12.0, 2.2));
        goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        goalSelector.add(3, new WanderAroundFarGoal(this, 0.8));
        goalSelector.add(4, new LookAroundGoal(this));
    }

    @Override public boolean cannotDespawn() { return true; }

    private static final class ApproachPlayerGoal extends Goal {
        private final VerityEntity verity;
        private final double speed;
        private final double searchDistance;
        private final double stopDistance;
        private PlayerEntity player;
        private int repathDelay;

        private ApproachPlayerGoal(VerityEntity verity, double speed, double searchDistance, double stopDistance) {
            this.verity = verity;
            this.speed = speed;
            this.searchDistance = searchDistance;
            this.stopDistance = stopDistance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        @Override public boolean canStart() {
            if (verity.getWorld().isClient) return false;
            player = verity.getWorld().getClosestPlayer(verity, searchDistance);
            return player != null && player.isAlive() && !player.isSpectator()
                && verity.squaredDistanceTo(player) > stopDistance * stopDistance;
        }

        @Override public boolean shouldContinue() {
            return player != null && player.isAlive() && !player.isSpectator()
                && verity.squaredDistanceTo(player) > stopDistance * stopDistance
                && verity.squaredDistanceTo(player) < (searchDistance + 6.0) * (searchDistance + 6.0);
        }

        @Override public void start() {
            repathDelay = 0;
            verity.getNavigation().startMovingTo(player, speed);
        }

        @Override public void stop() {
            player = null;
            verity.getNavigation().stop();
        }

        @Override public void tick() {
            if (player == null) return;
            verity.getLookControl().lookAt(player, 30.0f, 30.0f);
            if (--repathDelay <= 0) {
                repathDelay = 10;
                if (verity.squaredDistanceTo(player) > stopDistance * stopDistance) {
                    verity.getNavigation().startMovingTo(player, speed);
                } else {
                    verity.getNavigation().stop();
                }
            }
        }
    }
}
