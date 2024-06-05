package com.lthoerner.synapse.mixin.cauldron_dispenser;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.PowderSnowBucketItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBucketItem.class)
public abstract class PowderSnowBucketItemMixin {
    @Shadow
    @Final
    private SoundEvent placeSound;

    @Inject(
            method = "placeFluid(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/hit/BlockHitResult;)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private void synapse$allowDispenserSnowPlacementInCauldron(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        if (world.isInBuildLimit(pos) && world.getBlockState(pos).getBlock() == Blocks.CAULDRON) {
            if (!world.isClient) {
                world.setBlockState(pos, Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with(Properties.LEVEL_3, 3), Block.NOTIFY_ALL);
            }

            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            world.playSound(null, pos, this.placeSound, SoundCategory.BLOCKS, 1.0f, 1.0f);
            cir.setReturnValue(true);
        }
    }
}
