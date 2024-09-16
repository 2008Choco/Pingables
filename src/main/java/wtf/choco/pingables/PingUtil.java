package wtf.choco.pingables;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.HitResult;

public final class PingUtil {

    private PingUtil() { }

    public static HitResult getTargetPosition(Entity entity) {
        FluidState state = entity.level().getFluidState(BlockPos.containing(entity.getEyePosition()));
        boolean considerFluid = state.isEmpty();
        return entity.pick(100, 1.0F, considerFluid);
    }

}
