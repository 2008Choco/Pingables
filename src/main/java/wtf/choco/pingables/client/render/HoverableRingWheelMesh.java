package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;

public final class HoverableRingWheelMesh {

    private static final int COORDINATES_PER_VERTEX = 2;
    private static final float DEFAULT_OPACITY = 0.5F;

    private final float[] innerCoordinates;
    private final float[] outerCoordinates;
    private final float[] outerMidpointCoordinates;

    private final int resolution;
    private final float theta;
    private final float innerRadius;
    private final float outerRadius;
    private final int trueSegmentCount;
    private final int segmentColor;
    private final int segmentHoverColor;

    public HoverableRingWheelMesh(int resolution, float theta, float innerRadius, float outerRadius, int trueSegmentCount, int segmentColor, int segmentHoverColor) {
        this.resolution = resolution;
        this.theta = theta;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.trueSegmentCount = trueSegmentCount;
        this.segmentColor = segmentColor;
        this.segmentHoverColor = segmentHoverColor;

        // Adding +1 to the inner/outer coordinates resolution so we can append a copy of the first coordinates to the end for rendering
        this.innerCoordinates = new float[(resolution + 1) * COORDINATES_PER_VERTEX];
        this.outerCoordinates = new float[(resolution + 1) * COORDINATES_PER_VERTEX];
        this.outerMidpointCoordinates = new float[resolution * COORDINATES_PER_VERTEX];

        this.generateMesh();
    }

    private void generateMesh() {
        // Inner and outer circle vertices
        for (int i = 0; i < resolution; i++) {
            float angle = Mth.HALF_PI + (theta * i);
            float cos = -Mth.cos(angle);
            float sin = -Mth.sin(angle);

            int index = (i * COORDINATES_PER_VERTEX);
            this.innerCoordinates[index] = (innerRadius * cos);
            this.innerCoordinates[index + 1] = (innerRadius * sin);
            this.outerCoordinates[index] = (outerRadius * cos);
            this.outerCoordinates[index + 1] = (outerRadius * sin);
        }

        // The last triangle needs the first inner and outer coordinates, so we'll append them to the end of the array
        this.innerCoordinates[innerCoordinates.length - 2] = innerCoordinates[0];
        this.innerCoordinates[innerCoordinates.length - 1] = innerCoordinates[1];
        this.outerCoordinates[outerCoordinates.length - 2] = outerCoordinates[0];
        this.outerCoordinates[outerCoordinates.length - 1] = outerCoordinates[1];

        // Midpoint vertices
        for (int i = 0; i < resolution; i++) {
            int index = (i * COORDINATES_PER_VERTEX);

            float x1 = outerCoordinates[index];
            float y1 = outerCoordinates[index + 1];
            float x2 = outerCoordinates[(index + 2)];
            float y2 = outerCoordinates[(index + 3)];

            this.outerMidpointCoordinates[index] = (x1 + x2) / 2;
            this.outerMidpointCoordinates[index + 1] = (y1 + y2) / 2;
        }
    }

    public void render(MultiBufferSource buffer, PoseStack stack, int hoveredIndex) {
        int segmentsPerElement = (resolution / trueSegmentCount);

        Matrix4f pose = stack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(PingablesRenderTypes.guiTriangles());
        int alpha = ARGB.as8BitChannel(Minecraft.getInstance().options.getBackgroundOpacity(DEFAULT_OPACITY)) << 24;
        int segmentColor = alpha | (this.segmentColor);
        int segmentHoverColor = alpha | (this.segmentHoverColor);

        for (int i = 0; i < resolution; i++) {
            int index = (i *  COORDINATES_PER_VERTEX);
            int actualColor = segmentColor;

            // We have to consider the fact that higher-resolution circles will have more segments that need colouring!
            if ((i / segmentsPerElement) == hoveredIndex) {
                actualColor = (segmentColor & 0xFF000000) | segmentHoverColor;
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
