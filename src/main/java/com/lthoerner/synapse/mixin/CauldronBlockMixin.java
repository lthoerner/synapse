package com.lthoerner.synapse.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CauldronBlock.class)
public abstract class CauldronBlockMixin implements FluidFillable {
    @Override
    public boolean canFillWithFluid(@Nullable PlayerEntity player, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid == Fluids.WATER || fluid == Fluids.LAVA;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        Fluid fluid = fluidState.getFluid();
        if (fluid == Fluids.WATER) {
            world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(Properties.LEVEL_3, 3), Block.NOTIFY_ALL);
            return true;
        } else if (fluid == Fluids.LAVA) {
            world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState(), Block.NOTIFY_ALL);
        }

        return false;
    }
}
