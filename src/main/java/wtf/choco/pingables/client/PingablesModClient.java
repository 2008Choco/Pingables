package wtf.choco.pingables.client;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.HitResult;

import org.lwjgl.glfw.GLFW;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.network.ClientboundPayloadListener;
import wtf.choco.pingables.client.render.IdentifiedLayerPingIcon;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypes;
import wtf.choco.pingables.ping.PositionedPing;

public final class PingablesModClient extends PingablesMod {

    public static final KeyMapping KEY_PING = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key." + PingablesMod.MODID + ".ping",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category." + PingablesMod.MODID + ".ping"
    ));

    public void initClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KEY_PING.consumeClick()) {
                HitResult targetPosition = PingUtil.getTargetPosition(client.player);
                if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                    continue;
                }

                PingType pingType = Screen.hasShiftDown() ? PingTypes.HOME : PingTypes.GO_THERE;
                PositionedPing ping = new PositionedPing(pingType, targetPosition.getLocation(), client.player.getUUID(), System.currentTimeMillis());
                this.getPingTracker().addPing(ping);

                ClientPlayNetworking.send(new ServerboundPingPayload(pingType));
            }
        });

        HudLayerRegistrationCallback.EVENT.register(drawer -> drawer.attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, new IdentifiedLayerPingIcon(this)));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> getPingTracker().clearPings());

        ClientboundPayloadListener payloadListener = new ClientboundPayloadListener(this);
        payloadListener.registerIncomingHandlers();
    }

}
