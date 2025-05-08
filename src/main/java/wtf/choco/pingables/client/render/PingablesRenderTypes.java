package wtf.choco.pingables.client.render;

import net.minecraft.client.renderer.RenderType;

public final class PingablesRenderTypes {

    private static final RenderType.CompositeRenderType GUI_TRIANGLES = RenderType.create(
            "pingables:gui_triangles",
            RenderType.gui().bufferSize(), // We can just mirror the buffer size of the GUI render type
            PingablesRenderPipelines.GUI_TRIANGLES,
            RenderType.CompositeState.builder().createCompositeState(false)
    );

    private PingablesRenderTypes() { }

    public static RenderType guiTriangles() {
        return GUI_TRIANGLES;
    }

}
