package wtf.choco.pingables.network;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingPayload;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypeFilter;
import wtf.choco.pingables.ping.PositionedPing;
import wtf.choco.pingables.registry.PingablesRegistries;

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
            Holder<PingType> pingType = payload.pingType();

            PingTypeFilter filter = mod.getPingTypeFilter();
            boolean disallowedByFilter = (filter != null && !filter.contains(pingType));
            boolean notRegistered = !context.server().registryAccess().lookup(PingablesRegistries.PING_TYPE).flatMap(registry -> pingType.unwrapKey().map(registry::containsKey)).orElse(false);
            if (disallowedByFilter || notRegistered) {
                context.player().connection.disconnect(Component.translatable("pingables.error.invalid_ping_type", pingType.getRegisteredName()).withStyle(ChatFormatting.RED));
                return;
            }

            HitResult targetPosition = PingUtil.getTargetPosition(context.player());
            if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                return;
            }

            Vec3 position = targetPosition.getLocation();
            UUID placedBy = context.player().getUUID();
            long timestamp = System.currentTimeMillis();

            this.mod.getPingTracker().addPing(new PositionedPing(pingType, position, placedBy, timestamp));

            ClientboundSetPingPayload responsePayload = new ClientboundSetPingPayload(pingType, position, placedBy, timestamp);
            for (ServerPlayer player : PlayerLookup.tracking(context.player().serverLevel(), BlockPos.containing(position))) {
                ServerPlayNetworking.send(player, responsePayload);
            }
        });
    }

}
