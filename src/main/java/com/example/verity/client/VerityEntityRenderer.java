package com.example.verity.client;

import com.example.verity.VerityEntity;
import com.example.verity.VerityMod;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class VerityEntityRenderer extends EntityRenderer<VerityEntity> {
    private static final Identifier TEXTURE = new Identifier(VerityMod.MOD_ID, "textures/entity/smile_ball.png");
    public VerityEntityRenderer(EntityRendererFactory.Context context) { super(context); }

    @Override public void render(VerityEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                                 VertexConsumerProvider consumers, int light) {
        matrices.push();
        matrices.translate(0.0, 0.15, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f -
            MathHelper.lerpAngleDegrees(tickDelta, entity.prevYaw, entity.getYaw())));
        float speed = (float) entity.getVelocity().horizontalLength();
        float move = MathHelper.clamp(speed * 5.0f, 0.0f, 1.0f);
        float phase = (entity.age + tickDelta) * 0.72f;
        matrices.translate(0, Math.abs(Math.sin(phase)) * 0.10 * move, 0);
        float squash = (float)Math.max(0, Math.sin(phase * 2)) * 0.06f * move;
        matrices.scale(1 + squash * .3f, 1 - squash, 1 + squash * .3f);
        VertexConsumer vc = consumers.getBuffer(RenderLayer.getEntityCutoutNoCull(TEXTURE));
        SphereMesh.draw(matrices, vc, light);
        matrices.pop();
        super.render(entity, yaw, tickDelta, matrices, consumers, light);
    }
    @Override public Identifier getTexture(VerityEntity entity) { return TEXTURE; }
}
