package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;

public final class PingablesRenderPipelines {

    public static final RenderPipeline GUI_TRIANGLES = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .withLocation(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "pipeline/gui_triangles"))
            .build()
    );

    private PingablesRenderPipelines() { }

}
