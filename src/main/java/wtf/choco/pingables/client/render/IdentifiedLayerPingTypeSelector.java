package wtf.choco.pingables.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class IdentifiedLayerPingTypeSelector implements IdentifiedLayer {

    private boolean visible = false;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker delta) {
        if (!visible) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        double mouseX = minecraft.mouseHandler.getScaledXPos(minecraft.getWindow());
        double mouseY = minecraft.mouseHandler.getScaledYPos(minecraft.getWindow());
        double centerX = graphics.guiWidth() / 2.0;
        double centerY = graphics.guiHeight() / 2.0;
        double dx = (mouseX - centerX);
        double dy = (mouseY - centerY);
        double angle = ((-Mth.atan2(dx, dy) * Mth.RAD_TO_DEG) + 180); // 0deg = up, 90deg = right, 180deg = down, 270deg = left

        Component text = Component.literal("Mouse diff: (" + dx + ", " + dy + ", angle: " + angle + ")");
        int x = (graphics.guiWidth() - minecraft.font.width(text)) / 2;
        int y = (graphics.guiHeight() / 2) + minecraft.font.lineHeight;

        graphics.drawString(minecraft.font, text, x, y, 0xFFFFFFFF);
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

}
