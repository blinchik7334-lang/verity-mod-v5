package com.example.verity.mixin;

import com.example.verity.VerityState;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Хитбокс превращённого игрока: 1 x 1 x 1 блок (и на сервере, и на клиенте). */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Unique
    private boolean verity$small;

    // Каждый тик проверяем, не изменилось ли состояние, и пересчитываем хитбокс
    @Inject(method = "tick", at = @At("HEAD"))
    private void verity$tick(CallbackInfo ci) {
        PlayerEntity self = (PlayerEntity) (Object) this;
        boolean want = VerityState.isActive(self);
        if (want != verity$small) {
            verity$small = want;
            self.calculateDimensions();
        }
    }

    @Inject(method = "getDimensions", at = @At("HEAD"), cancellable = true, require = 0)
    private void verity$dimensions(EntityPose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        if (VerityState.isActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(EntityDimensions.fixed(1.0f, 1.0f));
        }
    }

    // Глаза (камера) внутри маленького хитбокса
    @Inject(method = "getActiveEyeHeight", at = @At("HEAD"), cancellable = true, require = 0)
    private void verity$eyeHeight(EntityPose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
        if (VerityState.isActive((PlayerEntity) (Object) this)) {
            cir.setReturnValue(0.8f);
        }
    }
}
