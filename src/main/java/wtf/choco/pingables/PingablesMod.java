package wtf.choco.pingables;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.choco.pingables.events.DisconnectFromServerCallback;
import wtf.choco.pingables.network.PingablesProtocol;
import wtf.choco.pingables.network.ServerboundPayloadHandler;
import wtf.choco.pingables.ping.PingTracker;
import wtf.choco.pingables.registry.PingablesRegistries;

public class PingablesMod {

    public static final String MODID = "pingables";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private final PingTracker pingTracker;

    public PingablesMod() {
        this.pingTracker = new PingTracker();
    }

    public void initCommon() {
        // Bootstrap
        PingablesRegistries.bootstrap();
        PingablesProtocol.bootstrap();

        // Network
        ServerboundPayloadHandler serverboundPayloadHandler = new ServerboundPayloadHandler(this);
        serverboundPayloadHandler.registerHandlers();

        // Register server-sided event callbacks
        ServerTickEvents.END_SERVER_TICK.register(pingTracker::tickServer);
        ServerPlayConnectionEvents.DISCONNECT.register(new DisconnectFromServerCallback(this));
    }

    public PingTracker getPingTracker() {
        return pingTracker;
    }

}
