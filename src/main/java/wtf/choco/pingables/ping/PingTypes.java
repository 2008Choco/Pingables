package wtf.choco.pingables.ping;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingTypes {

    public static final PingType GO_THERE = register("go_there");
    public static final PingType HOME = register("home");

    private static PingType register(String id) {
        ResourceLocation key = ResourceLocation.tryBuild(PingablesMod.MODID, id);
        ResourceLocation assetLocation = ResourceLocation.tryBuild(PingablesMod.MODID, "icons/" + id + ".png");

        return Registry.register(PingablesRegistries.PING_TYPE, key, new PingType(assetLocation));
    }

    public static void bootstrap() { }

}
