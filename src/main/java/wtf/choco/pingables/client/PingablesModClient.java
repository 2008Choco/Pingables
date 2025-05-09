package wtf.choco.pingables.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.event.RawInputEvent;
import wtf.choco.pingables.client.events.ClientDisconnectFromServerCallback;
import wtf.choco.pingables.client.events.MouseScrollPingWheelCallback;
import wtf.choco.pingables.client.input.PingablesKeyBindings;
import wtf.choco.pingables.client.network.ClientboundPayloadHandler;
import wtf.choco.pingables.client.render.IdentifiedLayerPingIcon;
import wtf.choco.pingables.client.render.IdentifiedLayerPingTypeSelector;

public final class PingablesModClient extends PingablesMod {

    private final IdentifiedLayerPingTypeSelector pingTypeSelector = new IdentifiedLayerPingTypeSelector();

    public void initClient() {
        // Bootstrap keybindings
        PingablesKeyBindings.bootstrap(this);

        // Register network handlers
        ClientboundPayloadHandler payloadListener = new ClientboundPayloadHandler(this);
        payloadListener.registerIncomingHandlers();

        this.attachHudLayers();

        // Register client-sided event callbacks
        RawInputEvent.MOUSE_SCROLL.register(new MouseScrollPingWheelCallback(this));
        ClientPlayConnectionEvents.DISCONNECT.register(new ClientDisconnectFromServerCallback(this));
    }

    public IdentifiedLayerPingTypeSelector getPingTypeSelector() {
        return pingTypeSelector;
    }

    private void attachHudLayers() {
        HudLayerRegistrationCallback.EVENT.register(drawer -> {
            drawer.attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, new IdentifiedLayerPingIcon(this));
            drawer.attachLayerAfter(IdentifiedLayer.CHAT, pingTypeSelector);
        });
    }

}
