package wtf.choco.pingables.network;

import com.google.common.base.Preconditions;

import java.util.Optional;
import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayPayloadHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingPayload;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PositionedPing;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class ServerboundPayloadListener {

    private boolean registered = false;

    private final PingablesMod mod;

    public ServerboundPayloadListener(PingablesMod mod) {
        this.mod = mod;
    }

    public void registerPayloads() {
        Preconditions.checkState(!registered, "Payloads have already been registered! Do not call this method twice!");

        // TODO: Do this automatically maybe? Somehow? I'm not sure how

        // Clientbound payloads
        PayloadTypeRegistry.playS2C().register(ClientboundSetPingPayload.TYPE, ClientboundSetPingPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ClientboundRemovePingPayload.TYPE, ClientboundRemovePingPayload.STREAM_CODEC);

        // Serverbound payloads
        this.registerServerboundPayload(ServerboundPingPayload.TYPE, ServerboundPingPayload.STREAM_CODEC, this::onPing);

        this.registered = true;
    }

    private void onPing(ServerboundPingPayload payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            HitResult targetPosition = PingUtil.getTargetPosition(context.player());
            if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                return;
            }

            Optional<Holder.Reference<PingType>> possiblePingType = PingablesRegistries.PING_TYPE.get(payload.pingType());
            if (possiblePingType.isEmpty()) {
                return;
            }

            PingType pingType = possiblePingType.get().value();
            Vec3 position = targetPosition.getLocation();
            UUID placedBy = context.player().getUUID();
            long timestamp = System.currentTimeMillis();

            this.mod.getPingTracker().addPing(new PositionedPing(pingType, position, placedBy, timestamp));

            ClientboundSetPingPayload responsePayload = new ClientboundSetPingPayload(payload.pingType(), position, placedBy, timestamp);
            for (ServerPlayer player : PlayerLookup.tracking(context.player().serverLevel(), BlockPos.containing(position))) {
                ServerPlayNetworking.send(player, responsePayload);
            }
        });
    }

    private <T extends CustomPacketPayload> void registerServerboundPayload(CustomPacketPayload.Type<T> type, StreamCodec<? super FriendlyByteBuf, T> codec, PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, handler);
    }

}
