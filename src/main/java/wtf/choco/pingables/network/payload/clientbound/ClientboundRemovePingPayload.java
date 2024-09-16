package wtf.choco.pingables.network.payload.clientbound;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;

public final record ClientboundRemovePingPayload(UUID target) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundRemovePingPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PingablesMod.MODID, "remove_ping"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundRemovePingPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ClientboundRemovePingPayload::write, ClientboundRemovePingPayload::new
    );

    public ClientboundRemovePingPayload(FriendlyByteBuf buffer) {
        this(buffer.readUUID());
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(target);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
