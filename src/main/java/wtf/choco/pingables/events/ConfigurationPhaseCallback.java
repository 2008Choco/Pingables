package wtf.choco.pingables.events;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundSetPingTypeFilterPayload;
import wtf.choco.pingables.ping.PingTypeFilter;

public final class ConfigurationPhaseCallback implements ServerConfigurationConnectionEvents.Configure {

    private final PingablesMod mod;

    public ConfigurationPhaseCallback(PingablesMod mod) {
        this.mod = mod;
    }

    @Override
    public void onSendConfiguration(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
        PingTypeFilter filter = mod.getPingTypeFilter();
        if (filter != null) {
            ServerConfigurationNetworking.send(handler, new ClientboundSetPingTypeFilterPayload(filter));
        }
    }

}
