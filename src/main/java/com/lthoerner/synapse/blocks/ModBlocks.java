package com.lthoerner.synapse.blocks;

import com.lthoerner.synapse.Synapse;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block CALIBRATED_REDSTONE_TORCH = registerBlock("calibrated_redstone_torch", new CalibratedRedstoneTorch());
    public static final Block CALIBRATED_REDSTONE_WALL_TORCH = registerBlock("calibrated_redstone_wall_torch", new CalibratedRedstoneWallTorch());

    private static void registerBlockGroups(FabricItemGroupEntries entries) {
        entries.add(CALIBRATED_REDSTONE_TORCH);
        entries.add(CALIBRATED_REDSTONE_WALL_TORCH);
    }

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Synapse.MOD_ID, name), block);
    }

    public static void registerModBlocks() {
        Synapse.LOGGER.info("Registering Synapse blocks");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(ModBlocks::registerBlockGroups);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModBlocks::registerBlockGroups);
    }
}

class CalibratedRedstoneTorch extends RedstoneTorchBlock {
    public CalibratedRedstoneTorch() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_TORCH));
    }
}

class CalibratedRedstoneWallTorch extends WallRedstoneTorchBlock {
    public CalibratedRedstoneWallTorch() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_WALL_TORCH));
    }
}
