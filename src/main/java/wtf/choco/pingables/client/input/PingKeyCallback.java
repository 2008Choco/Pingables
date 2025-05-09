package wtf.choco.pingables.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.HitResult;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.client.render.IdentifiedLayerPingTypeSelector;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PositionedPing;

public final class PingKeyCallback implements ClientTickEvents.EndTick {

    private final PingablesModClient mod;

    public PingKeyCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public void onEndTick(Minecraft client) {
        if (client.screen != null) {
            return;
        }

        // TODO: Simple press = default ping, press and hold = wheel
        if (PingablesKeyBindings.KEY_PING.isDown() && !mod.getPingTypeSelector().isVisible()) {
            this.mod.getPingTypeSelector().setVisible(true);
            client.mouseHandler.releaseMouse();
        } else if (!PingablesKeyBindings.KEY_PING.isDown() && mod.getPingTypeSelector().isVisible()) {
            IdentifiedLayerPingTypeSelector pingTypeSelector = mod.getPingTypeSelector();
            pingTypeSelector.setVisible(false);
            pingTypeSelector.getCurrentlyHoveredPingType().ifPresent(pingType -> dispatchPing(client, pingType));

            client.mouseHandler.grabMouse();
        }
    }

    private void dispatchPing(Minecraft client, Holder<PingType> pingType) {
        HitResult targetPosition = PingUtil.getTargetPosition(client.player);
        if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
            return;
        }

        PositionedPing ping = new PositionedPing(pingType, targetPosition.getLocation(), client.player.getUUID(), System.currentTimeMillis());
        this.mod.getPingTracker().addPing(ping);
        ClientPlayNetworking.send(new ServerboundPingPayload(pingType));
    }

}
