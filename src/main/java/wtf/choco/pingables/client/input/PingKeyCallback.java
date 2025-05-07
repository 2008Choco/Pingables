package wtf.choco.pingables.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Registry;
import net.minecraft.world.phys.HitResult;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypes;
import wtf.choco.pingables.ping.PositionedPing;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingKeyCallback implements ClientTickEvents.EndTick {

    private final PingablesModClient mod;

    public PingKeyCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public void onEndTick(Minecraft client) {
        while (PingablesKeyBindings.KEY_PING.consumeClick()) {
            HitResult targetPosition = PingUtil.getTargetPosition(client.player);
            if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                continue;
            }

            Registry<PingType> registry = client.level.registryAccess().lookupOrThrow(PingablesRegistries.PING_TYPE);
            registry.get(Screen.hasShiftDown() ? PingTypes.HOME : PingTypes.GO_THERE).ifPresent(pingType -> {
                PositionedPing ping = new PositionedPing(pingType, targetPosition.getLocation(), client.player.getUUID(), System.currentTimeMillis());
                this.mod.getPingTracker().addPing(ping);

                ClientPlayNetworking.send(new ServerboundPingPayload(pingType));
            });
        }
    }

}
