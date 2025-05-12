package wtf.choco.pingables.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.resources.ResourceKey;

import wtf.choco.pingables.client.input.PingablesKeyBindings;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PingTypes;

public final class PingablesLanguageProvider extends FabricLanguageProvider {

    protected PingablesLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(Provider registryLookup, TranslationBuilder builder) {
        this.addPingType(builder, PingTypes.GO_THERE, "Go There");
        this.addPingType(builder, PingTypes.HOME, "Home");

        builder.add(PingablesKeyBindings.CATEGORY_PINGABLES, "Pingables");
        builder.add(PingablesKeyBindings.KEY_PING.getName(), "Ping");

        builder.add("pingables.error.invalid_ping_type", "Sent invalid PingType: %s");
    }

    private void addPingType(TranslationBuilder builder, ResourceKey<PingType> key, String name) {
        builder.add(Util.makeDescriptionId("ping_type", key.location()), name);
    }

}
