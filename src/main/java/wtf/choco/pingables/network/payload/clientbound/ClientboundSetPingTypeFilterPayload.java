package wtf.choco.pingables.network.payload.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingTypeFilter;

public final record ClientboundSetPingTypeFilterPayload(PingTypeFilter filter) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSetPingTypeFilterPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "set_ping_type_filter"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetPingTypeFilterPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ClientboundSetPingTypeFilterPayload::write, ClientboundSetPingTypeFilterPayload::new
    );

    public ClientboundSetPingTypeFilterPayload(FriendlyByteBuf buffer) {
        this(PingTypeFilter.STREAM_CODEC.decode(buffer));
    }

    private void write(FriendlyByteBuf buffer) {
        PingTypeFilter.STREAM_CODEC.encode(buffer, filter);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
