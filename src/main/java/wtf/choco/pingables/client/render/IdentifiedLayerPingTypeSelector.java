package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.Optional;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;

import org.joml.Matrix4f;

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

    private static final int WHEEL_RESOLUTION = Mth.floor(PING_TYPES_PER_PAGE * 2); // How many vertices are used to generate the circle (must be a multiple of PING_TYPES_PER_PAGE)
    private static final float WHEEL_THETA = (Mth.TWO_PI / WHEEL_RESOLUTION);
    private static final float WHEEL_OUTER_RADIUS = 60F;
    private static final float WHEEL_INNER_RADIUS = 15F;
    private static final int WHEEL_SEGMENT_HOVER_COLOR = 0x43AB21;

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
        graphics.drawSpecial(buffer -> drawWheelBackground(minecraft, buffer, stack, hoveredPingTypeIndex, centerX, centerY));

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

    private void drawWheelBackground(Minecraft minecraft, MultiBufferSource buffer, PoseStack stack, int hoveredIndex, double centerX, double centerY) {
        Matrix4f pose = stack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(PingablesRenderTypes.guiTriangles());
        int color = minecraft.options.getBackgroundColor(Integer.MIN_VALUE);

        final int coordsPerVertex = 2;

        float[] outerCoordinates = new float[(WHEEL_RESOLUTION + 1) * coordsPerVertex]; // Adding +1 to the resolution so we can append the first coordinate to the end (see comment below)
        float[] innerCoordinates = new float[(WHEEL_RESOLUTION + 1) * coordsPerVertex];
        for (int i = 0; i < WHEEL_RESOLUTION; i++) {
            float angle = Mth.HALF_PI + (WHEEL_THETA * i);
            float cos = -Mth.cos(angle);
            float sin = -Mth.sin(angle);

            int index = (i * coordsPerVertex);
            outerCoordinates[index] = (float) centerX + (WHEEL_OUTER_RADIUS * cos);
            outerCoordinates[index + 1] = (float) centerY + (WHEEL_OUTER_RADIUS * sin);
            innerCoordinates[index] = (float) centerX + (WHEEL_INNER_RADIUS * cos);
            innerCoordinates[index + 1] = (float) centerY + (WHEEL_INNER_RADIUS * sin);
        }

        // The last triangle needs the first inner and outer coordinates, so we'll append them to the end of the array
        outerCoordinates[outerCoordinates.length - 2] = outerCoordinates[0];
        outerCoordinates[outerCoordinates.length - 1] = outerCoordinates[1];
        innerCoordinates[innerCoordinates.length - 2] = innerCoordinates[0];
        innerCoordinates[innerCoordinates.length - 1] = innerCoordinates[1];

        // Calculate the midpoints between each outer layer segment so we can draw 3 triangles per segment (\/\/ shape)
        float[] outerMidpointCoordinates = new float[WHEEL_RESOLUTION * coordsPerVertex];
        for (int i = 0; i < WHEEL_RESOLUTION; i++) {
            int index = (i * coordsPerVertex);

            float x1 = outerCoordinates[index];
            float y1 = outerCoordinates[index + 1];
            float x2 = outerCoordinates[(index + 2)];
            float y2 = outerCoordinates[(index + 3)];

            outerMidpointCoordinates[index] = (x1 + x2) / 2;
            outerMidpointCoordinates[index + 1] = (y1 + y2) / 2;
        }

        int segmentsPerPingType = (WHEEL_RESOLUTION / PING_TYPES_PER_PAGE);

        for (int i = 0; i < WHEEL_RESOLUTION; i++) {
            int index = (i *  coordsPerVertex);
            int actualColor = color;

            // We have to consider the fact that higher-resolution circles will have more segments that need colouring!
            if ((i / segmentsPerPingType) == hoveredIndex) {
                actualColor = (color & 0xFF000000) | WHEEL_SEGMENT_HOVER_COLOR;
            }

            // inner, midpoint, outer
            consumer.addVertex(pose, innerCoordinates[index], innerCoordinates[index + 1], 0).setColor(actualColor);
            consumer.addVertex(pose, outerMidpointCoordinates[index], outerMidpointCoordinates[index + 1], 0).setColor(actualColor);
            consumer.addVertex(pose, outerCoordinates[index], outerCoordinates[index + 1], 0).setColor(actualColor);

            // inner + 1, midpoint, inner
            consumer.addVertex(pose, innerCoordinates[index + 2], innerCoordinates[index + 3], 0).setColor(actualColor);
            consumer.addVertex(pose, outerMidpointCoordinates[index], outerMidpointCoordinates[index + 1], 0).setColor(actualColor);
            consumer.addVertex(pose, innerCoordinates[index], innerCoordinates[index + 1], 0).setColor(actualColor);

            // inner + 1, outer + 1, midpoint
            consumer.addVertex(pose, innerCoordinates[index + 2], innerCoordinates[index + 3], 0).setColor(actualColor);
            consumer.addVertex(pose, outerCoordinates[index + 2], outerCoordinates[index + 3], 0).setColor(actualColor);
            consumer.addVertex(pose, outerMidpointCoordinates[index], outerMidpointCoordinates[index + 1], 0).setColor(actualColor);
        }
    }

}
