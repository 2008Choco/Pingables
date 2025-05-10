package wtf.choco.pingables.client.input;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.phys.HitResult;

import wtf.choco.pingables.PingUtil;
import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.client.event.RawInputEvent;
import wtf.choco.pingables.client.gui.PingTypeSelectorWheel;
import wtf.choco.pingables.network.payload.serverbound.ServerboundPingPayload;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypes;
import wtf.choco.pingables.ping.PositionedPing;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingKeyCallback implements RawInputEvent.KeyMappingStateChange, ClientTickEvents.EndTick {

    private static final long HOLD_TICKS = 5;

    private long held = -1;

    private final PingablesModClient mod;

    public PingKeyCallback(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public void onStateChange(KeyMapping key, boolean down) {
        if (key != PingablesKeyBindings.KEY_PING) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null) {
            this.held = -1;
            return;
        }

        if (down) {
            this.held = 0;
        } if (!down) {
            boolean instantlyPing = (held < HOLD_TICKS);
            PingTypeSelectorWheel selector = mod.getLayers().getPingTypeSelectorWheel();

            if (instantlyPing) {
                minecraft.level.registryAccess().lookupOrThrow(PingablesRegistries.PING_TYPE).get(PingTypes.GO_THERE).ifPresent(pingType -> dispatchPing(minecraft, pingType));
            } else if (selector.isVisible()) {
                selector.setVisible(false);
                selector.getCurrentlyHoveredPingType().ifPresent(pingType -> dispatchPing(minecraft, pingType));
                minecraft.mouseHandler.grabMouse();
            }

            this.held = -1;
        }
    }

    @Override
    public void onEndTick(Minecraft minecraft) {
        if (held < 0) {
            return;
        }

        this.held++;
        if (held < HOLD_TICKS) {
            return;
        }

        PingTypeSelectorWheel selector = mod.getLayers().getPingTypeSelectorWheel();
        if (!selector.isVisible()) {
            selector.setVisible(true);
            minecraft.mouseHandler.releaseMouse();
        }
    }

    private void dispatchPing(Minecraft client, Holder<PingType> pingType) {
        HitResult targetPosition = PingUtil.getTargetPosition(client.player);
        if (targetPosition == null || targetPosition.getType() == HitResult.Type.MISS) {
            return;
        }

        PositionedPing ping = new PositionedPing(pingType, targetPosition.getLocation(), client.player.getUUID(), System.currentTimeMillis());
        this.mod.getPingTracker().addPing(ping);
        ClientPlayNetworking.send(new ServerboundPingPayload(pingType));
    }

}
