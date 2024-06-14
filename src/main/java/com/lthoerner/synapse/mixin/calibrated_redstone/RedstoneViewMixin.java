package com.lthoerner.synapse.mixin.calibrated_redstone;

import com.llamalad7.mixinextras.sugar.Local;
import com.lthoerner.synapse.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.world.RedstoneView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneView.class)
public interface RedstoneViewMixin {
    @Inject(
            method = "getEmittedRedstonePower(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Z)I",
            at = @At(value = "INVOKE", target = "net/minecraft/block/BlockState.isOf (Lnet/minecraft/block/Block;)Z"),
            cancellable = true
    )
    private void supportCalibratedRedstoneBlockGatePower(CallbackInfoReturnable<Integer> info, @Local(ordinal = 0) BlockState blockState) {
        if (blockState.isOf(ModBlocks.CALIBRATED_REDSTONE_BLOCK)) {
            info.setReturnValue(blockState.get(Properties.POWER));
        }
    }
}
