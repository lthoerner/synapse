package com.lthoerner.synapse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(JukeboxBlock.class)
public abstract class JukeboxBlockMixin {
    @ModifyReturnValue(
            method = "getWeakRedstonePower(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)I",
            at = @At("RETURN")
    )
    private int synapse$preventHopperPower(int original, BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // The return value is always either 0 or 15.
        if (original != 15) {
            return 0;
        }

        if (direction == Direction.UP && world.getBlockState(pos.down()).getBlock() instanceof HopperBlock) return 0;
            else return 15;
    }
}
