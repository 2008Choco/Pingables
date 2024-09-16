package wtf.choco.pingables.ping;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingType {

    private String translationKey;

    private final ResourceLocation assetLocation;

    public PingType(ResourceLocation assetLocation) {
        this.assetLocation = assetLocation;
    }

    public Component getName() {
        return Component.translatable(getTranslationKey());
    }

    public String getTranslationKey() {
        if (translationKey == null) {
            this.translationKey = Util.makeDescriptionId("ping_type", PingablesRegistries.PING_TYPE.getKey(this));
        }

        return translationKey;
    }

    public ResourceLocation getAssetLocation() {
        return assetLocation;
    }

}
