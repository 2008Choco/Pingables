package wtf.choco.pingables.ping;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.registry.PingablesRegistries;

public record PingType(ResourceLocation textureLocation, Component name) {

    public static final Codec<PingType> DIRECT_CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture_location").forGetter(PingType::textureLocation),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(PingType::name)
        ).apply(instance, PingType::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PingType> DIRECT_STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        PingType::textureLocation,
        ComponentSerialization.STREAM_CODEC,
        PingType::name,
        PingType::new
    );

    public static final Codec<Holder<PingType>> CODEC = RegistryFileCodec.create(PingablesRegistries.KEY_PING_TYPE, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PingType>> STREAM_CODEC = ByteBufCodecs.holder(PingablesRegistries.KEY_PING_TYPE, DIRECT_STREAM_CODEC);

}
