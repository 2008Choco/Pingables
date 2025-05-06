package wtf.choco.pingables.network.payload.clientbound;

import java.util.UUID;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PositionedPing;

public final record ClientboundSetPingPayload(Holder<PingType> pingType, Vec3 position, UUID placedBy, long timestamp) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSetPingPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PingablesMod.MODID, "set_ping"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetPingPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ClientboundSetPingPayload::write, ClientboundSetPingPayload::new
    );

    public ClientboundSetPingPayload(RegistryFriendlyByteBuf buffer) {
        this(PingType.STREAM_CODEC.decode(buffer), buffer.readVec3(), buffer.readUUID(), buffer.readLong());
    }

    @Nullable
    public PositionedPing getPositionedPing() {
        return new PositionedPing(pingType, position, placedBy, timestamp);
    }

    private void write(RegistryFriendlyByteBuf buffer) {
        PingType.STREAM_CODEC.encode(buffer, pingType);
        buffer.writeVec3(position);
        buffer.writeUUID(placedBy);
        buffer.writeLong(timestamp);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
