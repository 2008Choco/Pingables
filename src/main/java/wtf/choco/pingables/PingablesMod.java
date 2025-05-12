package wtf.choco.pingables;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wtf.choco.pingables.events.ConfigurationPhaseCallback;
import wtf.choco.pingables.events.DisconnectFromServerCallback;
import wtf.choco.pingables.network.PingablesProtocol;
import wtf.choco.pingables.network.ServerboundPayloadHandler;
import wtf.choco.pingables.ping.PingTracker;
import wtf.choco.pingables.ping.PingTypeFilter;
import wtf.choco.pingables.registry.PingablesRegistries;

public class PingablesMod implements ModInitializer {

    public static final String MODID = "pingables";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    private PingTypeFilter pingTypeFilter = null;

    private final PingTracker pingTracker;

    public PingablesMod() {
        this.pingTracker = new PingTracker();
    }

    @Override
    public void onInitialize() {
        // Bootstrap
        PingablesRegistries.bootstrap();
        PingablesProtocol.bootstrap();

        // Network
        ServerboundPayloadHandler serverboundPayloadHandler = new ServerboundPayloadHandler(this);
        serverboundPayloadHandler.registerHandlers();

        // Register server-sided event callbacks
        ServerTickEvents.END_SERVER_TICK.register(pingTracker::tickServer);
        ServerPlayConnectionEvents.DISCONNECT.register(new DisconnectFromServerCallback(this));
        ServerConfigurationConnectionEvents.CONFIGURE.register(new ConfigurationPhaseCallback(this));
    }

    public void setPingTypeFilter(PingTypeFilter pingTypeFilter) {
        this.pingTypeFilter = pingTypeFilter;
    }

    @Nullable
    public PingTypeFilter getPingTypeFilter() {
        return pingTypeFilter;
    }

    public PingTracker getPingTracker() {
        return pingTracker;
    }

}
