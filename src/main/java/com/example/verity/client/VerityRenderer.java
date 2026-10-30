package com.example.verity.client;

import com.example.verity.VerityClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public final class VerityRenderer {

    public static void render(VerityModels.Form form, AbstractClientPlayerEntity player,
                              float tickDelta, MatrixStack matrices,
                              VertexConsumerProvider consumers, int light) {
        if (player.isInvisible()) return;

        matrices.push();

        // Лицо смотрит туда же, куда смотрит игрок
        float headYaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevHeadYaw, player.headYaw);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - headYaw));

        // Анимация передвижения: Verity подпрыгивает и слегка сжимается при приземлении.
        // Скорость сглажена, чтобы анимация не дёргалась при старте и остановке.
        float horizontalSpeed = (float) player.getVelocity().horizontalLength();
        float movement = MathHelper.clamp(horizontalSpeed * 5.0f, 0.0f, 1.0f);
        float phase = (player.age + tickDelta) * 0.72f;
        float bounce = (float) Math.abs(Math.sin(phase)) * 0.12f * movement;
        float squash = (float) Math.max(0.0, Math.sin(phase * 2.0f)) * 0.07f * movement;
        matrices.translate(0.0f, bounce, 0.0f);
        matrices.scale(1.0f + squash * 0.35f, 1.0f - squash, 1.0f + squash * 0.35f);

        // Общий размер: 0.5f = вдвое меньше исходного (1.0f = диаметр 1.5 блока)
        float size = form.scale() * 0.5f;
        matrices.scale(size, size, size);

        // Пока игрок говорит — рот "хлопает" (открыт ~2/3 времени)
        Identifier texture = form.texture();
        if (form.talkTexture() != null && VerityClient.isTalking(player.getUuid())) {
            long t = System.currentTimeMillis();
            if ((t / 110) % 3 != 0) texture = form.talkTexture();
        }

        VertexConsumer vc = consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(texture));

        if (form.layer() == null) {
            // Гладкий круглый шар
            SphereMesh.draw(matrices, vc, light);
        } else {
            // Модель из боксов
            matrices.scale(-1.0f, -1.0f, 1.0f);
            matrices.translate(0.0f, -1.501f, 0.0f);
            ModelPart root = VerityModels.root(form);
            root.render(matrices, vc, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        }

        matrices.pop();
    }

    private VerityRenderer() {}
}
