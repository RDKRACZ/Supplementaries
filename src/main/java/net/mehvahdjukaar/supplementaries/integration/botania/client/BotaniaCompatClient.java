package net.mehvahdjukaar.supplementaries.integration.botania.client;

import net.minecraftforge.client.event.EntityRenderersEvent;

public class BotaniaCompatClient {
    public static void registerRenderLayers() {
        //ItemBlockRenderTypes.setRenderLayer(BotaniaCompatRegistry.TATER_IN_A_JAR.get(), RenderType.cutout());
    }

    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // event.registerBlockEntityRenderer(BotaniaCompatRegistry.TATER_IN_A_JAR_TILE.get(), TaterInAJarTileRenderer::new);
    }
}
