package wtf.choco.pingables.client.mixin;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import wtf.choco.pingables.client.event.RawInputEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @Redirect(
            method = "keyPress(JIIII)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;set(Lcom/mojang/blaze3d/platform/InputConstants$Key;Z)V")
    )
    private static void set(Key key, boolean down) {
        KeyMapping mapping = KeyMappingAccessor.getMap().get(key);
        if (mapping != null && mapping.isDown() != down) {
            RawInputEvent.KEY_MAPPING_STATE_CHANGE.invoker().onStateChange(mapping, down);
        }

        KeyMapping.set(key, down);
    }

}
