package com.lthoerner.synapse.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
    private static final VoxelShape UP_RAYCAST_SHAPE = INSIDE_SHAPE_FLIPPED;

    @ModifyExpressionValue(
            method = "getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;",
            at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction;DOWN:Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC)
    )
    private Direction synapse$allowUpHopperPlacement(Direction original, ItemPlacementContext ctx) {
        return ctx.getSide().getOpposite();
    }

    @Inject(
            method = "getOutlineShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void synapse$addUpHopperOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> info) {
        if (state.get(HopperBlock.FACING) == Direction.UP) {
            info.setReturnValue(UP_SHAPE);
        }
    }

    @Inject(
            method = "getRaycastShape(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/util/shape/VoxelShape;",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void synapse$addUpHopperRaycastShape(BlockState state, BlockView world, BlockPos pos, CallbackInfoReturnable<VoxelShape> info) {
        if (state.get(HopperBlock.FACING) == Direction.UP) {
            info.setReturnValue(UP_RAYCAST_SHAPE);
        }
    }


}
