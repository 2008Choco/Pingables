package wtf.choco.pingables.network.payload.clientbound;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.ping.PositionedPing;
import wtf.choco.pingables.registry.PingablesRegistries;

public final record ClientboundSetPingPayload(ResourceLocation pingType, Vec3 position, UUID placedBy, long timestamp) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ClientboundSetPingPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.tryBuild(PingablesMod.MODID, "set_ping"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetPingPayload> STREAM_CODEC = CustomPacketPayload.codec(
            ClientboundSetPingPayload::write, ClientboundSetPingPayload::new
    );

    public ClientboundSetPingPayload(FriendlyByteBuf buffer) {
        this(buffer.readResourceLocation(), buffer.readVec3(), buffer.readUUID(), buffer.readLong());
    }

    @Nullable
    public PositionedPing getPositionedPing() {
        PingType pingType = PingablesRegistries.PING_TYPE.get(pingType());
        if (pingType == null) {
            return null;
        }

        return new PositionedPing(pingType, position, placedBy, timestamp);
    }

    private void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(pingType);
        buffer.writeVec3(position);
        buffer.writeUUID(placedBy);
        buffer.writeLong(timestamp);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
