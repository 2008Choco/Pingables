package wtf.choco.pingables.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

import org.lwjgl.glfw.GLFW;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.client.event.RawInputEvent;

public final class PingablesKeyBindings {

    public static final String CATEGORY_PINGABLES = "key.categories." + PingablesMod.MODID;

    public static final KeyMapping KEY_PING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + PingablesMod.MODID + ".ping",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            CATEGORY_PINGABLES
    ));

    private PingablesKeyBindings() { }

    public static void bootstrap(PingablesModClient mod) {
        PingKeyCallback pingKeyCallback = new PingKeyCallback(mod);
        ClientTickEvents.END_CLIENT_TICK.register(pingKeyCallback);
        RawInputEvent.KEY_MAPPING_STATE_CHANGE.register(pingKeyCallback);
    }

}
