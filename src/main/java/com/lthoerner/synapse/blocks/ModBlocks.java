package com.lthoerner.synapse.blocks;

import com.lthoerner.synapse.Synapse;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
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
    public static final Block CALIBRATED_REDSTONE_BLOCK = registerBlock("calibrated_redstone_block", new CalibratedRedstoneBlock());
    public static final Block CALIBRATED_REDSTONE_TORCH = registerBlock("calibrated_redstone_torch", new CalibratedRedstoneTorch());
    public static final Block CALIBRATED_REDSTONE_WALL_TORCH = registerBlock("calibrated_redstone_wall_torch", new CalibratedRedstoneWallTorch());

    private static Block registerBlock(String name, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(Synapse.MOD_ID, name), block);
    }
}

class CalibratedRedstoneBlock extends RedstoneBlock {
    public CalibratedRedstoneBlock() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK));
        setDefaultState(getDefaultState().with(Properties.POWER, 15));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWER);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(Properties.POWER);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return Utility.calibratedRedstoneUseHandler(state, world, pos);
    }
}

class CalibratedRedstoneTorch extends RedstoneTorchBlock {
    public CalibratedRedstoneTorch() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_TORCH).luminance(Utility::getCalibratedRedstoneTorchLightLevel));
        setDefaultState(getDefaultState().with(Properties.POWER, 15));
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
        return Utility.calibratedRedstoneUseHandler(state, world, pos);
    }
}

class CalibratedRedstoneWallTorch extends WallRedstoneTorchBlock {
    public CalibratedRedstoneWallTorch() {
        super(FabricBlockSettings.copyOf(Blocks.REDSTONE_WALL_TORCH).luminance(Utility::getCalibratedRedstoneTorchLightLevel));
        setDefaultState(getDefaultState().with(Properties.POWER, 15));
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
        return Utility.calibratedRedstoneUseHandler(state, world, pos);
    }
}

class Utility {
    protected static ActionResult calibratedRedstoneUseHandler(BlockState state, World world, BlockPos pos) {
        // TODO: Consider preventing calibration for torches while unpowered
        if (world.isClient) {
            float pitch = state.cycle(Properties.POWER).get(Properties.POWER) == 0 ? 0.5f : 0.6f;
            world.playSoundAtBlockCenter(pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3f, pitch, true);
            return ActionResult.SUCCESS;
        }

        // The LIT property for torches is not cycled here because it is handled by the `shouldUnpower` methods.
        state = state.cycle(Properties.POWER);
        world.setBlockState(pos, state, Block.NOTIFY_ALL);

        return ActionResult.SUCCESS;
    }

    protected static int getCalibratedRedstoneTorchLightLevel(BlockState state) {
        if (state.get(Properties.LIT)) {
            int powerLevel = state.get(Properties.POWER);
            return Math.min(9, powerLevel / 2 + 2);
        }

        return 0;
    }
}
