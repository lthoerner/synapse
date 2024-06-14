package com.lthoerner.synapse.mixin.cauldron_dispenser;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {
    @ModifyExpressionValue(
            method = "placeFluid(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z",
            at = @At(value = "FIELD", target = "net/minecraft/item/BucketItem.fluid : Lnet/minecraft/fluid/Fluid;", ordinal = 4)
    )
    private Fluid allowFluidsForCauldron(Fluid original, PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult) {
        if (world.getBlockState(pos).getBlock() == Blocks.CAULDRON && original == Fluids.WATER || original == Fluids.LAVA) {
            // This return value bypasses a guard clause which would otherwise prevent a dispenser from filling a
            // cauldron with anything but water.
            return Fluids.WATER;
        } else {
            return original;
        }
    }
}
