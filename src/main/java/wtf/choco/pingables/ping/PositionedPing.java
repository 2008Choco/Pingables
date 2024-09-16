package wtf.choco.pingables.ping;

import java.util.UUID;

import net.minecraft.world.phys.Vec3;

public final record PositionedPing(PingType type, Vec3 position, UUID placedBy, long timestamp) {

}
