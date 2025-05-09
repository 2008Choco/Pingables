package wtf.choco.pingables.ping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.network.payload.clientbound.ClientboundRemovePingPayload;

public final class PingTracker {

    private static final long PING_EXPIRE_TIME_MS = TimeUnit.SECONDS.toMillis(8); // TODO: Make this configurable from the server

    private final Map<UUID, PositionedPing> playerPings = new HashMap<>();

    public void setPlayerPing(UUID playerUUID, PositionedPing ping) {
        if (ping == null) {
            this.playerPings.remove(playerUUID);
        } else {
            this.playerPings.put(playerUUID, ping);
        }
    }

    public boolean removePing(UUID playerUUID) {
        return playerPings.remove(playerUUID) != null;
    }

    public void addPing(PositionedPing ping) {
        this.setPlayerPing(ping.placedBy(), ping);
    }

    public Collection<PositionedPing> getPings() {
        return playerPings.values();
    }

    public void clearPings() {
        this.playerPings.clear();
    }

    public void tickServer(MinecraftServer server) {
        long now = System.currentTimeMillis();

        Iterator<PositionedPing> iterator = playerPings.values().iterator();
        while (iterator.hasNext()) {
            PositionedPing ping = iterator.next();
            if (now - ping.timestamp() < PING_EXPIRE_TIME_MS) {
                continue;
            }

            ClientboundRemovePingPayload payload = new ClientboundRemovePingPayload(ping.placedBy());
            for (ServerPlayer player : PlayerLookup.all(server)) {
                ServerPlayNetworking.send(player, payload);
            }

            iterator.remove();
            PingablesMod.LOGGER.info("Removing ping from " + ping.placedBy() + " because it expired!");
        }
    }

}
