package wtf.choco.pingables.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;

public final class PingablesRegistries {

    public static final ResourceKey<Registry<PingType>> PING_TYPE = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "ping_type"));

    private PingablesRegistries() { }

    public static void bootstrap() {
        DynamicRegistries.registerSynced(PING_TYPE, PingType.DIRECT_CODEC);
    }

}
