package wtf.choco.pingables.ping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PingTracker {

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

}
