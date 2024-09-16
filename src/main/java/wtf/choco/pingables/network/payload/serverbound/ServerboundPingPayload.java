package wtf.choco.pingables.network.payload.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.registry.PingablesRegistries;

public final record ServerboundPingPayload(ResourceLocation pingType) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ServerboundPingPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PingablesMod.MODID, "ping"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundPingPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ServerboundPingPayload::write, ServerboundPingPayload::new
    );

    public ServerboundPingPayload(PingType pingType) {
        this(PingablesRegistries.PING_TYPE.getKey(pingType));
    }

    public ServerboundPingPayload(FriendlyByteBuf buffer) {
        this(buffer.readResourceLocation());
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(pingType);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
