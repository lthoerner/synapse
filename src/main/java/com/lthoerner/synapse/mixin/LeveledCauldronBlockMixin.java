package com.lthoerner.synapse.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import static net.minecraft.block.LeveledCauldronBlock.LEVEL;

@Mixin(LeveledCauldronBlock.class)
public abstract class LeveledCauldronBlockMixin implements FluidDrainable {
    public ItemStack tryDrainFluid(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
        boolean powderSnowCauldron = state.getBlock() == Blocks.POWDER_SNOW_CAULDRON;
        if (state.get(LEVEL) == 3) {
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState(), Block.NOTIFY_ALL);

            if (powderSnowCauldron) return new ItemStack(Items.POWDER_SNOW_BUCKET);
            else return new ItemStack(Items.WATER_BUCKET);
        }
        return ItemStack.EMPTY;
    }
}
