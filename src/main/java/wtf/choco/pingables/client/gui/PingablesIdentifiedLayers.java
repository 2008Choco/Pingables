package wtf.choco.pingables.client.gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.PingablesModClient;

public final class PingablesIdentifiedLayers {

    public static final ResourceLocation PING_ICON = ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "ping_icon");
    public static final ResourceLocation PING_TYPE_SELECTOR_WHEEL = ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "ping_type_selector_wheel");

    private final PingablesModClient mod;
    private final IdentifiedLayerPingTypeSelectorWheel pingTypeSelectorWheel;

    public PingablesIdentifiedLayers(PingablesModClient mod) {
        this.mod = mod;
        this.pingTypeSelectorWheel = new IdentifiedLayerPingTypeSelectorWheel(mod);
    }

    public void bootstrap() {
        HudLayerRegistrationCallback.EVENT.register(drawer -> drawer
                .attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, new IdentifiedLayerPingIcon(mod))
                .attachLayerAfter(IdentifiedLayer.CHAT, pingTypeSelectorWheel)
        );
    }

    public PingTypeSelectorWheel getPingTypeSelectorWheel() {
        return pingTypeSelectorWheel;
    }

}
