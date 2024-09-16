package wtf.choco.pingables.registry;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;

public final class PingablesRegistries {

    public static final ResourceKey<Registry<PingType>> KEY_PING_TYPE = ResourceKey.createRegistryKey(ResourceLocation.tryBuild(PingablesMod.MODID, "ping_type"));

    public static final Registry<PingType> PING_TYPE = FabricRegistryBuilder.createSimple(KEY_PING_TYPE)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    private PingablesRegistries() { }

}
