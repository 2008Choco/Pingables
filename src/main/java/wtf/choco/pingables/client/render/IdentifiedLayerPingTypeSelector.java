package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import java.util.Optional;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;

import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class IdentifiedLayerPingTypeSelector implements IdentifiedLayer {

    private static final int PING_TYPE_ICON_SIZE = 18;
    private static final int SELECTED_PING_TYPE_ICON_SIZE = 22;
    private static final int PING_TYPES_PER_PAGE = 8;

    private static final double RADIANS_PER_PING_TYPE = (Math.TAU / PING_TYPES_PER_PAGE);
    private static final double INNER_RADIUS = 12;
    private static final double INNER_RADIUS_SQUARED = Mth.square(INNER_RADIUS);
    private static final double OUTER_RADIUS = 70;
    private static final double OUTER_RADIUS_SQUARED = Mth.square(OUTER_RADIUS);
    private static final double ICON_RADIUS_FROM_CENTER = (OUTER_RADIUS - INNER_RADIUS) / (Math.PI / 2);

    private static final double START_OFFSET_ANGLE = (-Math.PI / 2) + RADIANS_PER_PING_TYPE / 2;

    private static final int WHEEL_RESOLUTION = Mth.floor(PING_TYPES_PER_PAGE * 5); // How many vertices are used to generate the circle (must be a multiple of PING_TYPES_PER_PAGE)
    private static final float WHEEL_THETA = (Mth.TWO_PI / WHEEL_RESOLUTION);
    private static final float WHEEL_OUTER_RADIUS = 60F;
    private static final float WHEEL_INNER_RADIUS = 15F;
    private static final int WHEEL_SEGMENT_HOVER_COLOR = 0x696969; // 0x43AB21;

    private static final HoverableRingWheelMesh WHEEL_MESH = new HoverableRingWheelMesh(
            WHEEL_RESOLUTION,
            WHEEL_THETA,
            WHEEL_INNER_RADIUS,
            WHEEL_OUTER_RADIUS,
            PING_TYPES_PER_PAGE,
            0x000000,
            WHEEL_SEGMENT_HOVER_COLOR
    );

    private boolean visible = false;
    private Holder<PingType> currentlyHoveredPingType = null;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        if (!visible) {
            this.currentlyHoveredPingType = null;
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        double mouseX = minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
        double mouseY = minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
        double centerX = graphics.guiWidth() / 2.0;
        double centerY = graphics.guiHeight() / 2.0;
        double dx = (mouseX - centerX);
        double dy = (mouseY - centerY);
        double currentMouseAngle = calculateCurrentMouseAngleFromCenter(dx, dy);
        double currentMouseRadiusSquared = Mth.square(dx) + Mth.square(dy);

        int hoveredPingTypeIndex = isMouseInWheel(currentMouseRadiusSquared) ? Mth.floor(currentMouseAngle / RADIANS_PER_PING_TYPE) : -1;

        Profiler.get().push("pingTypeWheel");

        PoseStack stack = graphics.pose();
        graphics.drawSpecial(buffer -> {
            stack.pushPose();
            stack.translate(centerX, centerY, 0);
            WHEEL_MESH.render(buffer, stack, hoveredPingTypeIndex);
            stack.popPose();
        });

        Registry<PingType> registry = minecraft.level.registryAccess().lookupOrThrow(PingablesRegistries.PING_TYPE);
        IdMap<Holder<PingType>> idMap = registry.asHolderIdMap();

        double currentAngle = 0.0;
        int maxIterations = Math.min(PING_TYPES_PER_PAGE, idMap.size());
        Holder<PingType> currentlyHoveredPingType = null;

        for (int i = 0; i < maxIterations; i++, currentAngle += RADIANS_PER_PING_TYPE) {
            Holder<PingType> pingType = idMap.byIdOrThrow(i);

            int size = PING_TYPE_ICON_SIZE;
            if (i == hoveredPingTypeIndex) {
                currentlyHoveredPingType = pingType;
                size = SELECTED_PING_TYPE_ICON_SIZE;
            }

            int x = Mth.floor(centerX + (Math.cos(START_OFFSET_ANGLE + currentAngle) * ICON_RADIUS_FROM_CENTER) - (size / 2));
            int y = Mth.floor(centerY + (Math.sin(START_OFFSET_ANGLE + currentAngle) * ICON_RADIUS_FROM_CENTER) - (size / 2));

            graphics.blit(RenderType::guiTextured, pingType.value().textureLocation(), x, y, 0, 0, size, size, size, size);
        }

        Profiler.get().pop();

        this.currentlyHoveredPingType = currentlyHoveredPingType;
    }

    @Override
    public ResourceLocation id() {
        return PingablesIdentifiedLayers.PING_TYPE_SELECTOR;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public Optional<Holder<PingType>> getCurrentlyHoveredPingType() {
        return Optional.ofNullable(currentlyHoveredPingType);
    }

    // Returns a value in radians
    private double calculateCurrentMouseAngleFromCenter(double dx, double dy) {
        return Math.abs(Mth.atan2(dx, dy) - Math.PI); // 0 = up, PI/2 = right, PI = down, PI*3/2 = left
    }

    private boolean isMouseInWheel(double currentMouseRadiusSquared) {
        return (currentMouseRadiusSquared >= INNER_RADIUS_SQUARED && currentMouseRadiusSquared <= OUTER_RADIUS_SQUARED);
    }

}
