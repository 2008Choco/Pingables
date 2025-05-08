package wtf.choco.pingables.client.render;

import net.minecraft.client.renderer.RenderType;

public final class PingablesRenderTypes {

    private static final RenderType.CompositeRenderType GUI_TRIANGLE_STRIP = RenderType.create(
            "pingables:gui_triangle_strip",
            RenderType.gui().bufferSize(), // We can just mirror the buffer size of the GUI render type
            PingablesRenderPipelines.GUI_TRIANGLE_STRIP,
            RenderType.CompositeState.builder().createCompositeState(false)
    );

    private PingablesRenderTypes() { }

    public static RenderType guiTriangleStrip() {
        return GUI_TRIANGLE_STRIP;
    }

}
