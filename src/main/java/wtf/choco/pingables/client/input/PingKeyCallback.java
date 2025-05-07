package wtf.choco.pingables.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

import wtf.choco.pingables.client.PingablesModClient;

public final class PingKeyCallback implements ClientTickEvents.EndTick {

    private final PingablesModClient mod;

    public PingKeyCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public void onEndTick(Minecraft client) {
        if (client.screen != null) {
            return;
        }

        if (PingablesKeyBindings.KEY_PING.isDown() && !mod.getPingTypeSelector().isVisible()) {
            client.mouseHandler.releaseMouse();
            this.mod.getPingTypeSelector().setVisible(true);
        } else if (!PingablesKeyBindings.KEY_PING.isDown() && mod.getPingTypeSelector().isVisible()) {
            client.mouseHandler.grabMouse();
            this.mod.getPingTypeSelector().setVisible(false);
        }

        /*
        while (PingablesKeyBindings.KEY_PING.consumeClick()) {
            HitResult targetPosition = PingUtil.getTargetPosition(client.player);
            if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
                continue;
            }

            Registry<PingType> registry = client.level.registryAccess().lookupOrThrow(PingablesRegistries.PING_TYPE);
            registry.get(Screen.hasShiftDown() ? PingTypes.HOME : PingTypes.GO_THERE).ifPresent(pingType -> {
                PositionedPing ping = new PositionedPing(pingType, targetPosition.getLocation(), client.player.getUUID(), System.currentTimeMillis());
                this.mod.getPingTracker().addPing(ping);

                ClientPlayNetworking.send(new ServerboundPingPayload(pingType));
            });
        }
        */
    }

}
