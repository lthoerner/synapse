package com.lthoerner.synapse.mixin.hopper;

import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Properties.class)
public abstract class PropertiesMixin {
    @Shadow
    public static final DirectionProperty HOPPER_FACING = Properties.FACING;
}
