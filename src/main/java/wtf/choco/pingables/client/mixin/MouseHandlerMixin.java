package wtf.choco.pingables.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.MouseHandler;

import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import wtf.choco.pingables.client.event.RawInputEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(
            method = "onScroll(JDD)V",
            at = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z",
                shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    @SuppressWarnings("unused") // windowId, horizontalScroll, verticalScroll
    private void onScroll(long windowId, double horizontalScroll, double verticalScroll, CallbackInfo callback, @Local Vector2i scroll) {
        if (!RawInputEvent.MOUSE_SCROLL.invoker().onMouseScroll(scroll.x, scroll.y)) {
            callback.cancel();
        }
    }

    @Redirect(
            method = "onPress(JIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set")
    )
    private static void set(Key key, boolean down) {
        KeyMapping mapping = KeyMappingAccessor.getMap().get(key);
        if (mapping != null) {
            RawInputEvent.KEY_MAPPING_STATE_CHANGE.invoker().onStateChange(mapping, down);
        }

        KeyMapping.set(key, down);
    }

}
