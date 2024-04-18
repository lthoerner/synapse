package com.lthoerner.synapse.blocks;

import com.lthoerner.synapse.Synapse;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

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
        setDefaultState(getDefaultState().with(Properties.POWER, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, Properties.POWER);
    }

    @Override
    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        return world.isEmittingRedstonePower(pos.down(), Direction.DOWN) || state.get(Properties.POWER) == 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        boolean lit = state.get(LIT);
        int power = state.get(Properties.POWER);
        if (lit && power > 0 && Direction.UP != direction) {
            return power;
        }
        return 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return Utility.calibratedRedstoneTorchUseHandler(state, world, pos);
    }
}

class CalibratedRedstoneWallTorch extends WallRedstoneTorchBlock {
    public CalibratedRedstoneWallTorch() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_WALL_TORCH));
        setDefaultState(getDefaultState().with(Properties.POWER, 1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, Properties.POWER);
    }

    @Override
    protected boolean shouldUnpower(World world, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING).getOpposite();
        return world.isEmittingRedstonePower(pos.offset(direction), direction) || state.get(Properties.POWER) == 0;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        boolean lit = state.get(LIT);
        int power = state.get(Properties.POWER);
        if (lit && power > 0 && state.get(FACING) != direction) {
            return power;
        }
        return 0;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return Utility.calibratedRedstoneTorchUseHandler(state, world, pos);
    }
}

class Utility {
    protected static ActionResult calibratedRedstoneTorchUseHandler(BlockState state, World world, BlockPos pos) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        // The LIT property is not cycled here because it is handled by the `shouldUnpower` methods.
        state = state.cycle(Properties.POWER);

        world.setBlockState(pos, state, Block.NOTIFY_ALL);
        world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), SoundCategory.BLOCKS, 0.5f, 0.5f, true);

        return ActionResult.SUCCESS;
    }
}
