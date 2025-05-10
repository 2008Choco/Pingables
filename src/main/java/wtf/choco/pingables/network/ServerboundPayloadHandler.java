package wtf.choco.pingables.network;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingPayload;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PositionedPing;

public final class ServerboundPayloadHandler {

    private final PingablesMod mod;

    public ServerboundPayloadHandler(PingablesMod mod) {
        this.mod = mod;
    }

    public void registerHandlers() {
        ServerPlayNetworking.registerGlobalReceiver(ServerboundPingPayload.TYPE, this::onPing);
    }

    private void onPing(ServerboundPingPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            HitResult targetPosition = PingUtil.getTargetPosition(context.player());
            if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                return;
            }

            Vec3 position = targetPosition.getLocation();
            UUID placedBy = context.player().getUUID();
            long timestamp = System.currentTimeMillis();

            this.mod.getPingTracker().addPing(new PositionedPing(payload.pingType(), position, placedBy, timestamp));

            ClientboundSetPingPayload responsePayload = new ClientboundSetPingPayload(payload.pingType(), position, placedBy, timestamp);
            for (ServerPlayer player : PlayerLookup.tracking(context.player().serverLevel(), BlockPos.containing(position))) {
                ServerPlayNetworking.send(player, responsePayload);
            }
        });
    }

}
