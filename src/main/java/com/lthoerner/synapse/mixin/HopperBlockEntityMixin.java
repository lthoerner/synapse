package com.lthoerner.synapse.mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(value = HopperBlockEntity.class, priority = 1500)
public abstract class HopperBlockEntityMixin {
    @Unique
    private static final Box INPUT_AREA_SHAPE_FLIPPED = Block.createCuboidShape(0.0, -32.0, 0.0, 16.0, 5.0, 16.0).getBoundingBoxes().get(0);

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
    private static double synapse$getUpHopperInputInventoryVanilla(double originalY, World world, Hopper hopper, BlockPos pos, BlockState state) {
        if (isMinecartTrack(world, hopper)) return originalY;

        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), originalY, hopper.getHopperZ());
        BlockState hopperState = world.getBlockState(hopperPos);
        // Need to subtract 2 here instead of 1 because the vanilla code adds 1 to the result of this call
        if (hopperState.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) return originalY - 2.0;
        else return originalY;
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
    private static double synapse$getUpHopperInputInventoryFabric(double originalY, World world, Hopper hopper, CallbackInfoReturnable cir) {
        if (isMinecartTrack(world, hopper)) return originalY;

        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), originalY, hopper.getHopperZ());
        BlockState hopperState = world.getBlockState(hopperPos);
        // Need to subtract 2 here instead of 1 because the vanilla code adds 1 to the result of this call
        if (hopperState.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) return originalY - 2.0;
        else return originalY;
    }

    @ModifyExpressionValue(
            method = "getInputItemEntities(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "net/minecraft/block/entity/Hopper.getInputAreaShape ()Lnet/minecraft/util/math/Box;")
    )
    private static Box synapse$getUpHopperInputAreaShapeForItemEntityCheck(Box original, World world, Hopper hopper) {
        if (isMinecartTrack(world, hopper)) return original;

        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        BlockState hopperState = world.getBlockState(hopperPos);
        if (hopperState.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) return INPUT_AREA_SHAPE_FLIPPED;
        else return original;
    }

    @ModifyExpressionValue(
            method = "onEntityCollided(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/Entity;Lnet/minecraft/block/entity/HopperBlockEntity;)V",
            at = @At(value = "INVOKE", target = "net/minecraft/block/entity/HopperBlockEntity.getInputAreaShape ()Lnet/minecraft/util/math/Box;")
    )
    private static Box synapse$getUpHopperInputAreaShapeForEntityCollision(Box original, World world, BlockPos pos, BlockState state, Entity entity, HopperBlockEntity blockEntity) {
        if (isMinecartTrack(world, blockEntity)) return original;

        if (state.get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) return INPUT_AREA_SHAPE_FLIPPED;
        else return original;
    }

    @ModifyExpressionValue(
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At(value = "INVOKE", target = "net/minecraft/util/math/BlockPos.ofFloored (DDD)Lnet/minecraft/util/math/BlockPos;")
    )
    private static BlockPos synapse$allowUpHopperExtractionBlock(BlockPos original, World world, Hopper hopper) {
        if (isMinecartTrack(world, hopper)) return original;

        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        if (world.getBlockState(hopperPos).get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM) return hopperPos.down();
        else return hopperPos.up();
    }

    @ModifyExpressionValue(
            method = "extract(Lnet/minecraft/world/World;Lnet/minecraft/block/entity/Hopper;)Z",
            at = @At(value = "FIELD", target = "Lnet/minecraft/util/math/Direction;DOWN:Lnet/minecraft/util/math/Direction;", opcode = Opcodes.GETSTATIC)
    )
    private static Direction synapse$allowUpHopperExtractionDirection(Direction original, World world, Hopper hopper) {
        if (isMinecartTrack(world, hopper)) return original;
        BlockPos hopperPos = BlockPos.ofFloored(hopper.getHopperX(), hopper.getHopperY(), hopper.getHopperZ());
        if (world.getBlockState(hopperPos).get(Properties.BLOCK_HALF) == BlockHalf.BOTTOM)
            return Direction.UP;
        else return Direction.DOWN;
    }

    @Unique
    private static boolean isMinecartTrack(World world, Hopper hopperOrMinecartTrack) {
        BlockPos hopperPos = BlockPos.ofFloored(hopperOrMinecartTrack.getHopperX(), hopperOrMinecartTrack.getHopperY(), hopperOrMinecartTrack.getHopperZ());
        return !(world.getBlockState(hopperPos).getBlock() instanceof HopperBlock);
    }
}
