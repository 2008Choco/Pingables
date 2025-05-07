package wtf.choco.pingables.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.PingablesModClient;

public final class PingablesKeyBindings {

    public static final KeyMapping KEY_PING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + PingablesMod.MODID + ".ping",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category." + PingablesMod.MODID + ".ping"
    ));

    private PingablesKeyBindings() { }

    public static void bootstrap(PingablesModClient mod) {
        ClientTickEvents.END_CLIENT_TICK.register(new PingKeyCallback(mod));
    }

}
