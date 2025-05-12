package wtf.choco.pingables.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking.Context;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingPayload;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingTypeFilterPayload;
import wtf.choco.pingables.ping.PositionedPing;

public final class ClientboundPayloadHandler {

    private final PingablesModClient mod;

    public ClientboundPayloadHandler(PingablesModClient mod) {
        this.mod = mod;
    }

    public void registerHandlers() {
        // Configuration
        ClientConfigurationNetworking.registerGlobalReceiver(ClientboundSetPingTypeFilterPayload.TYPE, this::onConfigurationSetPingTypeFilter);

        // Play
        ClientPlayNetworking.registerGlobalReceiver(ClientboundSetPingPayload.TYPE, this::onSetPing);
        ClientPlayNetworking.registerGlobalReceiver(ClientboundRemovePingPayload.TYPE, this::onRemovePing);
    }

    private void onConfigurationSetPingTypeFilter(ClientboundSetPingTypeFilterPayload payload, Context context) {
        context.client().execute(() -> mod.setPingTypeFilter(payload.filter()));
    }

    private void onSetPing(ClientboundSetPingPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            PositionedPing ping = payload.getPositionedPing();
            if (ping == null) {
                return;
            }

            this.mod.getPingTracker().addPing(ping);
        });
    }

    private void onRemovePing(ClientboundRemovePingPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> mod.getPingTracker().removePing(payload.target()));
    }

}
