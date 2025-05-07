package wtf.choco.pingables.events;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;

public class DisconnectFromServerCallback implements ServerPlayConnectionEvents.Disconnect {

    private final PingablesMod mod;

    public DisconnectFromServerCallback(PingablesMod mod) {
        this.mod = mod;
    }

    @Override
    public void onPlayDisconnect(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        if (!mod.getPingTracker().removePing(handler.getOwner().getId())) {
            return;
        }

        ClientboundRemovePingPayload payload = new ClientboundRemovePingPayload(handler.getOwner().getId());
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

}
