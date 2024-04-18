package com.lthoerner.synapse.item;

import com.lthoerner.synapse.Synapse;
import com.lthoerner.synapse.blocks.ModBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.VerticallyAttachableBlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ModItems {
    public static final Item CALIBRATED_REDSTONE_TORCH = Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, "calibrated_redstone_torch"), new VerticallyAttachableBlockItem(ModBlocks.CALIBRATED_REDSTONE_TORCH, ModBlocks.CALIBRATED_REDSTONE_WALL_TORCH, new FabricItemSettings(), Direction.DOWN));

    private static void registerItemGroups(FabricItemGroupEntries entries) {
        entries.add(CALIBRATED_REDSTONE_TORCH);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(Synapse.MOD_ID, name), item);
    }

    public static void registerModItems() {
        Synapse.LOGGER.info("Registering Synapse items");
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(ModItems::registerItemGroups);
    }
}
