package com.lthoerner.synapse.mixin.jukebox;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxBlockEntity.class)
public abstract class JukeboxBlockEntityMixin extends BlockEntity {
    public JukeboxBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "startPlaying()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void doNotStartSongIfBlockAbove(CallbackInfo info) {
        if (world != null && world.getBlockState(pos.up()).isSolidBlock(world, pos.up())) {
            info.cancel();
        }
    }

    @ModifyExpressionValue(
            method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/block/entity/JukeboxBlockEntity.isSongFinished (Lnet/minecraft/item/MusicDiscItem;)Z")
    )
    private boolean stopSongIfBlockAbove(boolean original, World world, BlockPos pos, BlockState state) {
        return original || world.getBlockState(pos.up()).isSolidBlock(world, pos.up());
    }
}
