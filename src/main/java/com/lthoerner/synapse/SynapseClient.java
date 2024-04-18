package com.lthoerner.synapse;

import com.lthoerner.synapse.blocks.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SynapseClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap layerMap = BlockRenderLayerMap.INSTANCE;
        RenderLayer cutout = RenderLayer.getCutout();
        layerMap.putBlock(ModBlocks.CALIBRATED_REDSTONE_TORCH, cutout);
        layerMap.putBlock(ModBlocks.CALIBRATED_REDSTONE_WALL_TORCH, cutout);
    }
}
