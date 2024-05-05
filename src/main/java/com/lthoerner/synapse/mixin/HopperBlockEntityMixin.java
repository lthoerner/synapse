package com.lthoerner.synapse.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {
    @ModifyVariable(
            method = "setTransferCooldown (I)V",
            at = @At("HEAD"), ordinal = 0
    )
    private int synapse$setTransferCooldownTo1(int original) {
        return 0;
    }
}
