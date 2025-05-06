package wtf.choco.pingables.client.mixin;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Invoker("getFov")
    public float invokeGetFov(Camera camera, float tickDelta, boolean bl);

}
