package wtf.choco.pingables.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.events.ClientDisconnectFromServerCallback;
import wtf.choco.pingables.client.input.PingablesKeyBindings;
import wtf.choco.pingables.client.network.ClientboundPayloadHandler;
import wtf.choco.pingables.client.render.IdentifiedLayerPingIcon;

public final class PingablesModClient extends PingablesMod {

    public void initClient() {
        // Bootstrap keybindings
        PingablesKeyBindings.bootstrap(this);

        // Register network handlers
        ClientboundPayloadHandler payloadListener = new ClientboundPayloadHandler(this);
        payloadListener.registerIncomingHandlers();

        this.attachHudLayers();

        // Register client-sided event callbacks
        ClientPlayConnectionEvents.DISCONNECT.register(new ClientDisconnectFromServerCallback(this));
    }

    private void attachHudLayers() {
        HudLayerRegistrationCallback.EVENT.register(drawer -> {
            drawer.attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, new IdentifiedLayerPingIcon(this));
        });
    }

}
