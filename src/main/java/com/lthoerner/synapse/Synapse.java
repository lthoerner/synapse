package com.lthoerner.synapse;

import com.lthoerner.synapse.blocks.ModBlocks;
import com.lthoerner.synapse.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Synapse implements ModInitializer {
    public static final String MOD_ID = "synapse";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Synapse v0.0.1+1.20.4");

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();
    }
}
