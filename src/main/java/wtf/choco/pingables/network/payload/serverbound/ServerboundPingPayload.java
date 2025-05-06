package wtf.choco.pingables.network.payload.serverbound;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;

public final record ServerboundPingPayload(Holder<PingType> pingType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundPingPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PingablesMod.MODID, "ping"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPingPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ServerboundPingPayload::write, ServerboundPingPayload::new
    );

    public ServerboundPingPayload(RegistryFriendlyByteBuf buffer) {
        this(PingType.STREAM_CODEC.decode(buffer));
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        PingType.STREAM_CODEC.encode(buffer, pingType);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
