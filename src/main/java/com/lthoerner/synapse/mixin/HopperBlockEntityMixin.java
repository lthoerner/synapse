package com.lthoerner.synapse.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(value = HopperBlockEntity.class, priority = 1500)
public abstract class HopperBlockEntityMixin {
    @ModifyVariable(
            method = "setTransferCooldown (I)V",
            at = @At("HEAD"), ordinal = 0,
            argsOnly = true
    )
    private int synapse$setTransferCooldownTo1(int original) {
        return 0;
    }

    @ModifyExpressionValue(
            method = "getInputInventory(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Lnet/minecraft/inventory/Inventory;",
            at = @At(value = "INVOKE", target = "net/minecraft/block/entity/Hopper.getHopperY ()D")
    )
    private static double synapse$getUpHopperInputInventoryVanilla(double original, World world, Hopper hopper, BlockPos pos, BlockState state) {
        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        BlockState hopperState = world.getBlockState(hopperPos);
        if (hopperState.get(Properties.FACING) == Direction.UP) return hopper.getHopperY() - 1.0;
        else return hopper.getHopperY() + 1.0;
    }

    @SuppressWarnings({"InvalidMemberReference", "UnresolvedMixinReference", "MixinAnnotationTarget"})
    @TargetHandler(
            mixin = "net.fabricmc.fabric.mixin.transfer.HopperBlockEntityMixin",
            name = "hookExtract"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(value = "INVOKE", target = "net/minecraft/block/entity/Hopper.getHopperY ()D")
    )
    private static double synapse$getUpHopperInputInventoryFabric(double original, World world, Hopper hopper, CallbackInfoReturnable cir) {
        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        BlockState hopperState = world.getBlockState(hopperPos);
        if (hopperState.get(Properties.FACING) == Direction.UP) return hopper.getHopperY() - 1.0;
        else return hopper.getHopperY() + 1.0;
    }

    @ModifyExpressionValue(
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At(value = "INVOKE", target = "net/minecraft/util/math/BlockPos.ofFloored (DDD)Lnet/minecraft/util/math/BlockPos;")
    )
    private static BlockPos synapse$allowUpHopperExtractionBlock(BlockPos original, World world, Hopper hopper) {
        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        if (world.getBlockState(hopperPos).get(Properties.FACING) == Direction.UP) return hopperPos.down();
        else return hopperPos.up();
    }

    @ModifyExpressionValue(
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction;DOWN:Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC)
    )
    private static Direction synapse$allowUpHopperExtractionDirection(Direction original, World world, Hopper hopper) {
        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        if (world.getBlockState(hopperPos).get(Properties.FACING) == Direction.UP)
            return Direction.UP;
        else return Direction.DOWN;
    }
}
