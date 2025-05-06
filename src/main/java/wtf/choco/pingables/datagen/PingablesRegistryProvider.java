package wtf.choco.pingables.datagen;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingablesRegistryProvider extends FabricDynamicRegistryProvider {

    public PingablesRegistryProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(Provider registries, Entries entries) {
        entries.addAll(asLookup(entries.getLookup(PingablesRegistries.PING_TYPE)));
    }

    private <T> HolderLookup.RegistryLookup<T> asLookup(HolderGetter<T> getter) {
        return (HolderLookup.RegistryLookup<T>) getter;
    }

    @Override
    public String getName() {
        return PingablesMod.MODID + " Dynamic Registries";
    }

}
