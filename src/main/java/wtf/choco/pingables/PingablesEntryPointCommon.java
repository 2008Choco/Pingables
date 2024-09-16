package wtf.choco.pingables;

import net.fabricmc.api.ModInitializer;

public final class PingablesEntryPointCommon implements ModInitializer {

    @Override
    public void onInitialize() {
        PingablesMod mod = new PingablesMod();
        mod.initCommon();
    }

}
