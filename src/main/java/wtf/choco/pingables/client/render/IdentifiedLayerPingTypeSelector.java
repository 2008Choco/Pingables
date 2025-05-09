package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;

import java.text.NumberFormat;
import java.util.Optional;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;

import org.spongepowered.include.com.google.common.base.Preconditions;

import wtf.choco.pingables.ping.PingType;
import wtf.choco.pingables.registry.PingablesRegistries;

public final class IdentifiedLayerPingTypeSelector implements IdentifiedLayer {

    private static final int PING_TYPE_ICON_SIZE = 18;
    private static final int HOVERED_PING_TYPE_ICON_SIZE = 22;
    private static final int PING_TYPES_PER_PAGE = 8;

    private static final float RADIANS_PER_PING_TYPE = (Mth.TWO_PI / PING_TYPES_PER_PAGE);
    private static final float START_OFFSET_ANGLE = -Mth.HALF_PI + (RADIANS_PER_PING_TYPE / 2);

    private static final int WHEEL_RESOLUTION = Mth.floor(PING_TYPES_PER_PAGE * 5); // How many vertices are used to generate the circle (must be a multiple of PING_TYPES_PER_PAGE)
    private static final float WHEEL_THETA = (Mth.TWO_PI / WHEEL_RESOLUTION);
    private static final float ICON_RADIUS_FROM_CENTER = 40F;
    private static final float WHEEL_OUTER_RADIUS = 60F;
    private static final float WHEEL_INNER_RADIUS = 15F;
    private static final float WHEEL_INNER_RADIUS_SQUARED = Mth.square(WHEEL_INNER_RADIUS);
    private static final float WHEEL_OUTER_RADIUS_SQUARED = Mth.square(WHEEL_OUTER_RADIUS);

    private static final HoverableRingWheelMesh WHEEL_MESH = new HoverableRingWheelMesh(
            WHEEL_RESOLUTION,
            WHEEL_THETA,
            WHEEL_INNER_RADIUS,
            WHEEL_OUTER_RADIUS,
            PING_TYPES_PER_PAGE,
            0x000000,
            0x696969 // nice
    );

    private boolean visible = false;
    private int page = 0;
    private int maxPage = 0;
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
        this.updateMaxPage(registry);

        int maxIterations = Math.min(PING_TYPES_PER_PAGE, registry.size());
        PingType currentlyHoveredPingType = null;

        for (int i = 0; i < maxIterations; i++) {
            int pageOffset = (page * PING_TYPES_PER_PAGE);
            PingType pingType = registry.byId(i + pageOffset);
            if (pingType == null) {
                break;
            }

            int size = PING_TYPE_ICON_SIZE;
            if (i == hoveredPingTypeIndex) {
                currentlyHoveredPingType = pingType;
                size = HOVERED_PING_TYPE_ICON_SIZE;
            }

            double angle = START_OFFSET_ANGLE + (i * RADIANS_PER_PING_TYPE);
            int x = Mth.floor(centerX + (Math.cos(angle) * ICON_RADIUS_FROM_CENTER) - (size / 2));
            int y = Mth.floor(centerY + (Math.sin(angle) * ICON_RADIUS_FROM_CENTER) - (size / 2));

            graphics.blit(RenderType::guiTextured, pingType.textureLocation(), x, y, 0, 0, size, size, size, size);
        }

        if (currentlyHoveredPingType != null) {
            Component text = currentlyHoveredPingType.name();
            int x = Mth.floor(centerX - (minecraft.font.width(text) / 2));
            int y = Mth.floor(centerY + WHEEL_OUTER_RADIUS + minecraft.font.lineHeight);
            graphics.drawString(minecraft.font, text, x, y, 0xFFFFFFFF);
        }

        if (maxPage > 0) {
            Component text = Component.literal("Page: " + NumberFormat.getIntegerInstance().format(page + 1) + "/" + NumberFormat.getIntegerInstance().format(maxPage + 1));
            int x = Mth.floor(centerX - (minecraft.font.width(text) / 2));
            int y = Mth.floor(centerY - WHEEL_OUTER_RADIUS - (minecraft.font.lineHeight * 2));
            graphics.drawString(minecraft.font, text, x, y, 0xFFFFFFFF);
        }

        Profiler.get().pop();

        this.currentlyHoveredPingType = (currentlyHoveredPingType != null) ? registry.wrapAsHolder(currentlyHoveredPingType) : null;
    }

    @Override
    public ResourceLocation id() {
        return PingablesIdentifiedLayers.PING_TYPE_SELECTOR;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            this.setPage(0);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setPage(int page) {
        Preconditions.checkArgument(page >= 0 && page <= maxPage, "page must be between 0 and maxPage (%s), inclusive", maxPage);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    private void updateMaxPage(Registry<PingType> registry) {
        this.maxPage = Math.max((registry.size() - 1) / PING_TYPES_PER_PAGE, 0);
        this.page = Mth.clamp(page, 0, maxPage);
    }

    public int getMaxPage() {
        return maxPage;
    }

    public Optional<Holder<PingType>> getCurrentlyHoveredPingType() {
        return Optional.ofNullable(currentlyHoveredPingType);
    }

    // Returns a value in radians
    private double calculateCurrentMouseAngleFromCenter(double dx, double dy) {
        return Math.abs(Mth.atan2(dx, dy) - Math.PI); // 0 = up, PI/2 = right, PI = down, PI*3/2 = left
    }

    private boolean isMouseInWheel(double currentMouseRadiusSquared) {
        return (currentMouseRadiusSquared >= WHEEL_INNER_RADIUS_SQUARED && currentMouseRadiusSquared <= WHEEL_OUTER_RADIUS_SQUARED);
    }

}
