package wtf.choco.pingables.ping;

import java.util.UUID;

import net.minecraft.core.Holder;
import net.minecraft.world.phys.Vec3;

public final record PositionedPing(Holder<PingType> type, Vec3 position, UUID placedBy, long timestamp) {

}
