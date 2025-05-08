package wtf.choco.pingables.ping;

import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingTypes {

    public static final ResourceKey<PingType> GO_THERE = register("go_there");
    public static final ResourceKey<PingType> HOME = register("home");

    private static ResourceKey<PingType> register(String id) {
        return ResourceKey.create(PingablesRegistries.PING_TYPE, ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, id));
    }

    public static void bootstrap(BootstrapContext<PingType> context) {
        context.register(GO_THERE, create(GO_THERE));
        context.register(HOME, create(HOME));
    }

    private static PingType create(ResourceKey<PingType> key) {
        ResourceLocation textureLocation = ResourceLocation.tryBuild(PingablesMod.MODID, "icons/" + key.location().getPath() + ".png");
        return PingType.builder()
                .textureLocation(textureLocation)
                .name(Component.translatable(Util.makeDescriptionId("ping_type", key.location())))
                .build();
    }

}
