package wtf.choco.pingables.client.render;

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

        Registry<PingType> registry = minecraft.level.registryAccess().lookupOrThrow(PingablesRegistries.PING_TYPE);
        IdMap<Holder<PingType>> idMap = registry.asHolderIdMap();

        double currentAngle = 0.0;
        int maxIterations = Math.min(PING_TYPES_PER_PAGE, idMap.size());
        Holder<PingType> currentlyHoveredPingType = null;

        for (int i = 0; i < maxIterations; i++, currentAngle += RADIANS_PER_PING_TYPE) {
            Holder<PingType> pingType = idMap.byIdOrThrow(i);

            int size = PING_TYPE_ICON_SIZE;
            boolean isInWheel = (currentMouseRadiusSquared >= INNER_RADIUS_SQUARED && currentMouseRadiusSquared <= OUTER_RADIUS_SQUARED);
            boolean isInCurrentSegment = (currentMouseAngle >= currentAngle && currentMouseAngle <= (currentAngle + RADIANS_PER_PING_TYPE));
            if (isInWheel && isInCurrentSegment) {
                currentlyHoveredPingType = pingType;
                size = SELECTED_PING_TYPE_ICON_SIZE;
            }

            int x = Mth.floor(centerX + (Math.cos(START_OFFSET_ANGLE + currentAngle) * ICON_RADIUS_FROM_CENTER) - (size / 2));
            int y = Mth.floor(centerY + (Math.sin(START_OFFSET_ANGLE + currentAngle) * ICON_RADIUS_FROM_CENTER) - (size / 2));

            graphics.blit(RenderType::guiTextured, pingType.value().textureLocation(), x, y, 0, 0, size, size, size, size);
        }

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

    private double calculateCurrentMouseAngleFromCenter(double dx, double dy) {
        return Math.abs(Mth.atan2(dx, dy) - Math.PI); // 0deg = up, 90deg = right, 180deg = down, 270deg = left, but in radians
    }

}
