package wtf.choco.pingables.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator.Pack;
import net.minecraft.core.RegistrySetBuilder;

import wtf.choco.pingables.ping.PingTypes;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class PingablesDatagen implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        Pack pack = generator.createPack();

        pack.addProvider(PingablesRegistryProvider::new);
        pack.addProvider(PingablesLanguageProvider::new);
    }

    @Override
    public void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);

        registryBuilder.add(PingablesRegistries.PING_TYPE, PingTypes::bootstrap);
    }

}
