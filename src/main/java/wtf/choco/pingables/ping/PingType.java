package wtf.choco.pingables.ping;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
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

    public static final Codec<Holder<PingType>> CODEC = RegistryFixedCodec.create(PingablesRegistries.PING_TYPE);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<PingType>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PingablesRegistries.PING_TYPE);

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private ResourceLocation textureLocation;
        private Component name;

        public Builder textureLocation(ResourceLocation textureLocation) {
            this.textureLocation = textureLocation;
            return this;
        }

        public Builder name(Component name) {
            this.name = name;
            return this;
        }

        public PingType build() {
            Preconditions.checkState(textureLocation != null, "textureLocation must not be null");
            Preconditions.checkState(name != null, "name must not be null");

            return new PingType(textureLocation, name);
        }

    }

}
