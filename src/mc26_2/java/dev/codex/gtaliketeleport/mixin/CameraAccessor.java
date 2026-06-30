package dev.codex.gtaliketeleport.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setPosition")
    void gtalikeTeleport$setPosition(Vec3 pos);

    @Invoker("setRotation")
    void gtalikeTeleport$setRotation(float yaw, float pitch);

    @Invoker("createProjectionMatrixForCulling")
    Matrix4f gtalikeTeleport$createProjectionMatrixForCulling();

    @Invoker("prepareCullFrustum")
    void gtalikeTeleport$prepareCullFrustum(Matrix4fc viewRotationMatrix, Matrix4f projectionMatrix, Vec3 position);
}
