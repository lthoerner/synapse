package com.lthoerner.synapse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixin {
    @Unique
    private static final VoxelShape TOP_SHAPE_FLIPPED = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0);
    @Unique
    private static final VoxelShape MIDDLE_SHAPE_FLIPPED = Block.createCuboidShape(4.0, 6.0, 4.0, 12.0, 12.0, 12.0);
    @Unique
    private static final VoxelShape OUTSIDE_SHAPE_FLIPPED = VoxelShapes.union(MIDDLE_SHAPE_FLIPPED, TOP_SHAPE_FLIPPED);
    @Unique
    private static final VoxelShape INSIDE_SHAPE_FLIPPED = Block.createCuboidShape(2.0, 0.0, 2.0, 14.0, 5.0, 14.0);
    @Unique
    private static final VoxelShape DEFAULT_SHAPE_FLIPPED = VoxelShapes.combineAndSimplify(OUTSIDE_SHAPE_FLIPPED, INSIDE_SHAPE_FLIPPED, BooleanBiFunction.ONLY_FIRST);
    @Unique
    private static final VoxelShape UP_SHAPE = VoxelShapes.union(DEFAULT_SHAPE_FLIPPED, Block.createCuboidShape(6.0, 12.0, 6.0, 10.0, 16.0, 10.0));
    @Unique
    private static final VoxelShape EAST_SHAPE_FLIPPED = VoxelShapes.union(DEFAULT_SHAPE_FLIPPED, Block.createCuboidShape(12.0, 8.0, 6.0, 16.0, 12.0, 10.0));
    @Unique
    private static final VoxelShape NORTH_SHAPE_FLIPPED = VoxelShapes.union(DEFAULT_SHAPE_FLIPPED, Block.createCuboidShape(6.0, 8.0, 0.0, 10.0, 12.0, 4.0));
    @Unique
    private static final VoxelShape SOUTH_SHAPE_FLIPPED = VoxelShapes.union(DEFAULT_SHAPE_FLIPPED, Block.createCuboidShape(6.0, 8.0, 12.0, 10.0, 12.0, 16.0));
    @Unique
    private static final VoxelShape WEST_SHAPE_FLIPPED = VoxelShapes.union(DEFAULT_SHAPE_FLIPPED, Block.createCuboidShape(0.0, 8.0, 6.0, 4.0, 12.0, 10.0));
    @Unique
    private static final VoxelShape UP_RAYCAST_SHAPE = INSIDE_SHAPE_FLIPPED;
    @Unique
    private static final VoxelShape EAST_RAYCAST_SHAPE_FLIPPED = VoxelShapes.union(INSIDE_SHAPE_FLIPPED, Block.createCuboidShape(12.0, 6.0, 6.0, 16.0, 8.0, 10.0));
    @Unique
    private static final VoxelShape NORTH_RAYCAST_SHAPE_FLIPPED = VoxelShapes.union(INSIDE_SHAPE_FLIPPED, Block.createCuboidShape(6.0, 6.0, 0.0, 10.0, 8.0, 4.0));
    @Unique
    private static final VoxelShape SOUTH_RAYCAST_SHAPE_FLIPPED = VoxelShapes.union(INSIDE_SHAPE_FLIPPED, Block.createCuboidShape(6.0, 6.0, 12.0, 10.0, 8.0, 16.0));
    @Unique
    private static final VoxelShape WEST_RAYCAST_SHAPE_FLIPPED = VoxelShapes.union(INSIDE_SHAPE_FLIPPED, Block.createCuboidShape(0.0, 6.0, 6.0, 4.0, 8.0, 10.0));

    @Inject(
            method = "appendProperties(Lnet/minecraft/state/StateManager$Builder;)V",
            at = @At(value = "TAIL")
    )
    private void synapse$addBlockHalfHopperProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(Properties.BLOCK_HALF);
    }

    @ModifyArg(
            method = "<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/block/HopperBlock.setDefaultState (Lnet/minecraft/block/BlockState;)V"),
            index = 0
    )
    private BlockState synapse$addBlockHalfHopperDefaultState(BlockState original) {
        return original.with(Properties.BLOCK_HALF, BlockHalf.TOP);
    }

    @ModifyExpressionValue(
            method = "getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction;DOWN:Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC)
    )
    private Direction synapse$allowVerticalPlacement(Direction original, ItemPlacementContext ctx) {
        return ctx.getSide().getOpposite();
    }

    @ModifyExpressionValue(
            method = "getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;",
            at = @At(value = "INVOKE", target = "net/minecraft/block/HopperBlock.getDefaultState ()Lnet/minecraft/block/BlockState;")
    )
    private BlockState synapse$addPlacementBlockHalf(BlockState original, ItemPlacementContext ctx) {
        double placementVerticalPos = ctx.getHitPos().y - ctx.getBlockPos().getY();
        // The block half is inverted because a downward-facing hopper has block half TOP (the block half is the half
        // that represents the input of the hopper)
        return original.with(Properties.BLOCK_HALF, placementVerticalPos > 0.5 ? BlockHalf.BOTTOM : BlockHalf.TOP);
    }

    @Inject(
            method = "getOutlineShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void synapse$addUpHopperOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
        if (state.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) {
            info.setReturnValue(synapse$getHopperShape(state.get(HopperBlock.FACING), false));
        }
    }

    @Inject(
            method = "getRaycastShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void synapse$addUpHopperRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> info) {
        if (state.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) {
            info.setReturnValue(synapse$getHopperShape(state.get(HopperBlock.FACING), true));
        }
    }

    @Unique
    private static VoxelShape synapse$getHopperShape(Direction facing, boolean raycast) {
        return switch (facing) {
            case UP -> raycast ? UP_RAYCAST_SHAPE : UP_SHAPE;
            case NORTH -> raycast ? NORTH_RAYCAST_SHAPE_FLIPPED : NORTH_SHAPE_FLIPPED;
            case SOUTH -> raycast ? SOUTH_RAYCAST_SHAPE_FLIPPED : SOUTH_SHAPE_FLIPPED;
            case EAST -> raycast ? EAST_RAYCAST_SHAPE_FLIPPED : EAST_SHAPE_FLIPPED;
            case WEST -> raycast ? WEST_RAYCAST_SHAPE_FLIPPED : WEST_SHAPE_FLIPPED;
            default -> UP_RAYCAST_SHAPE;
        };

    }
}
