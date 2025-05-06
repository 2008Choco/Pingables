package wtf.choco.pingables.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import wtf.choco.pingables.PingablesMod;
import wtf.choco.pingables.client.PingablesModClient;
import wtf.choco.pingables.client.mixin.GameRendererAccessor;
import wtf.choco.pingables.ping.PositionedPing;

public final class IdentifiedLayerPingIcon implements IdentifiedLayer {

    private static final int ICON_SIZE = 16;

    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "ping_icon");

    private final PingablesModClient mod;

    public IdentifiedLayerPingIcon(PingablesModClient mod) {
        this.mod = mod;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = minecraft.gameRenderer.getMainCamera();
        float fov = ((GameRendererAccessor) minecraft.gameRenderer).invokeGetFov(camera, delta.getGameTimeDeltaPartialTick(true), true);

        for (PositionedPing ping : mod.getPingTracker().getPings()) {
            this.renderIcon(minecraft, graphics, ping, camera, fov);
        }
    }

    private void renderIcon(Minecraft minecraft, GuiGraphics graphics, PositionedPing ping, Camera camera, float fov) {
        double cameraX = camera.getPosition().x();
        double cameraY = camera.getPosition().y();
        double cameraZ = camera.getPosition().z();

        double deltaX = (ping.position().x() - cameraX);
        double deltaY = (ping.position().y() - cameraY);
        double deltaZ = (ping.position().z() - cameraZ);

        Vector4f position = new Vector4f((float) deltaX, (float) deltaY, (float) deltaZ, 1.0F);
        Quaternionf rotation = new Quaternionf();
        camera.rotation().conjugate(rotation);
        position.rotate(rotation);
        position.rotateY(Mth.PI);

        boolean behind = position.z <= 0.0F;

        Matrix4f projectionMatrix = minecraft.gameRenderer.getProjectionMatrix(fov);
        position.mulProject(projectionMatrix);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int halfScreenWidth = (screenWidth / 2);
        int halfScreenHeight = (screenHeight / 2);

        double addX = (position.x * halfScreenWidth);
        double addY = (position.y * halfScreenHeight);

        // If the icon is behind us, inverse the way that it slides across the screen
        if (behind) {
            addX *= -1;

            // And depending on whether or not we're above or below the icon, lock it either at the top or the bottom
            if (deltaY <= 0) {
                addY = halfScreenHeight;
            } else {
                addY = -halfScreenHeight;
            }
        }

        double x = halfScreenWidth + addX;
        double y = halfScreenHeight + addY;

        // Lock the icon to the viewport. It should always be visible even if not in our frustum
        x = Mth.clamp(x, (ICON_SIZE / 2), screenWidth - (ICON_SIZE / 2));
        y = Mth.clamp(y, (ICON_SIZE / 2), screenHeight - (ICON_SIZE / 2));

        int drawX = (int) Math.round(x - (ICON_SIZE / 2));
        int drawY = (int) Math.round(y - (ICON_SIZE / 2));
        // (texture, x, y, u, v, width, height, textureWidth, textureHeight)
        graphics.blit(RenderType::guiTextured, ping.type().getAssetLocation(), drawX, drawY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

}
