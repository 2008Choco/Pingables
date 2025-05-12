package wtf.choco.pingables.client.events;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import wtf.choco.pingables.client.PingablesModClient;

public final class ClientDisconnectFromServerCallback implements ClientPlayConnectionEvents.Disconnect {

    private final PingablesModClient mod;

    public ClientDisconnectFromServerCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public void onPlayDisconnect(ClientPacketListener listener, Minecraft client) {
        this.mod.getPingTracker().clearPings();
        this.mod.setPingTypeFilter(null);
    }

}
