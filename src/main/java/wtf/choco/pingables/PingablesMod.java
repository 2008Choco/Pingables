package wtf.choco.pingables;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.choco.pingables.network.ServerboundPayloadListener;
import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;
import wtf.choco.pingables.ping.PingTracker;
import wtf.choco.pingables.ping.PingTypes;

public class PingablesMod {

    public static final String MODID = "pingables";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private final PingTracker pingTracker;

    public PingablesMod() {
        this.pingTracker = new PingTracker();
    }

    public void initCommon() {
        PingTypes.bootstrap();

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (!getPingTracker().removePing(handler.getOwner().getId())) {
                return;
            }

            ClientboundRemovePingPayload payload = new ClientboundRemovePingPayload(handler.getOwner().getId());
            for (ServerPlayer player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, payload);
            }
        });

        ServerboundPayloadListener payloadListener = new ServerboundPayloadListener(this);
        payloadListener.registerPayloads();
    }

    public PingTracker getPingTracker() {
        return pingTracker;
    }

}
