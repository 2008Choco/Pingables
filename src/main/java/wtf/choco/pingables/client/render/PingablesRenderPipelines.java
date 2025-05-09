package wtf.choco.pingables.client.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import wtf.choco.pingables.PingablesMod;

public final class PingablesRenderPipelines {

    private static final Snippet GUI_TRIANGLE_STRIP_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withVertexShader(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "core/gui_triangle_strip"))
            .withFragmentShader(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "core/gui_triangle_strip"))
            .withBlend(BlendFunction.TRANSLUCENT)
            .buildSnippet();

    public static final RenderPipeline GUI_TRIANGLE_STRIP = RenderPipelines.register(RenderPipeline.builder(GUI_TRIANGLE_STRIP_SNIPPET)
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP)
            .withLocation(ResourceLocation.fromNamespaceAndPath(PingablesMod.MODID, "pipeline/gui_triangle_strip"))
            .build()
    );

    private PingablesRenderPipelines() { }

}
