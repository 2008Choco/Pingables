package wtf.choco.pingables.client;

import net.fabricmc.api.ClientModInitializer;

public final class PingablesEntryPointClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PingablesModClient mod = new PingablesModClient();
        mod.initClient();
    }

}
