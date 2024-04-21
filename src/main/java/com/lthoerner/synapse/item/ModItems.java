package com.lthoerner.synapse.item;

import com.lthoerner.synapse.Synapse;
import com.lthoerner.synapse.blocks.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModItems {
    public static final Item CALIBRATED_REDSTONE_BLOCK = registerBlockItem("calibrated_redstone_block", ModBlocks.CALIBRATED_REDSTONE_BLOCK);
    public static final Item CALIBRATED_REDSTONE_TORCH = registerVerticallyAttachableBlockItem("calibrated_redstone_torch", ModBlocks.CALIBRATED_REDSTONE_TORCH, ModBlocks.CALIBRATED_REDSTONE_WALL_TORCH);

    private static void setupRedstoneItems(FabricItemGroupEntries entries) {
        entries.addAfter(Items.REDSTONE_BLOCK, CALIBRATED_REDSTONE_TORCH, CALIBRATED_REDSTONE_BLOCK);
    }

    private static void setupFunctionalItems(FabricItemGroupEntries entries) {
        entries.addAfter(Items.REDSTONE_TORCH, CALIBRATED_REDSTONE_TORCH);
    }

    private static void setupBuildingBlockItems(FabricItemGroupEntries entries) {
        entries.addAfter(Items.REDSTONE_BLOCK, CALIBRATED_REDSTONE_BLOCK);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, name), item);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    private static Item registerVerticallyAttachableBlockItem(String name, Block block, Block verticallyAttachableBlock) {
        return Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, name), new VerticallyAttachableBlockItem(block, verticallyAttachableBlock, new FabricItemSettings(), Direction.DOWN));
    }

    public static void setupItemGroups() {
        Synapse.LOGGER.info("Registering Synapse items");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(ModItems::setupRedstoneItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::setupFunctionalItems);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(ModItems::setupBuildingBlockItems);
    }
}
