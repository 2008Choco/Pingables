package wtf.choco.pingables.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.event.RawInputEvent;
import wtf.choco.pingables.client.events.ClientDisconnectFromServerCallback;
import wtf.choco.pingables.client.events.MouseScrollPingWheelCallback;
import wtf.choco.pingables.client.gui.PingablesIdentifiedLayers;
import wtf.choco.pingables.client.input.PingablesKeyBindings;
import wtf.choco.pingables.client.network.ClientboundPayloadHandler;

public final class PingablesModClient extends PingablesMod {

    private final PingablesIdentifiedLayers layers = new PingablesIdentifiedLayers(this);

    public void initClient() {
        // Bootstrap
        PingablesKeyBindings.bootstrap(this);
        this.layers.bootstrap();

        // Network
        ClientboundPayloadHandler clientboundPayloadHandler = new ClientboundPayloadHandler(this);
        clientboundPayloadHandler.registerHandlers();

        // Register client-sided event callbacks
        RawInputEvent.MOUSE_SCROLL.register(new MouseScrollPingWheelCallback(this));
        ClientPlayConnectionEvents.DISCONNECT.register(new ClientDisconnectFromServerCallback(this));
    }

    public PingablesIdentifiedLayers getLayers() {
        return layers;
    }

}
