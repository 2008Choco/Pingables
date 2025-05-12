package wtf.choco.pingables.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingPayload;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingTypeFilterPayload;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;

public final class PingablesProtocol {

    public static void bootstrap() {
        // Configuration phase
        PayloadTypeRegistry<FriendlyByteBuf> clientboundConfig = PayloadTypeRegistry.configurationS2C();
        clientboundConfig.register(ClientboundSetPingTypeFilterPayload.TYPE, ClientboundSetPingTypeFilterPayload.STREAM_CODEC);

        // Play phase
        PayloadTypeRegistry<RegistryFriendlyByteBuf> clientbound = PayloadTypeRegistry.playS2C();
        clientbound.register(ClientboundSetPingPayload.TYPE, ClientboundSetPingPayload.STREAM_CODEC);
        clientbound.register(ClientboundRemovePingPayload.TYPE, ClientboundRemovePingPayload.STREAM_CODEC);
        clientbound.register(ClientboundSetPingTypeFilterPayload.TYPE, ClientboundSetPingTypeFilterPayload.STREAM_CODEC);

        PayloadTypeRegistry<RegistryFriendlyByteBuf> serverbound = PayloadTypeRegistry.playC2S();
        serverbound.register(ServerboundPingPayload.TYPE, ServerboundPingPayload.STREAM_CODEC);
    }

}
