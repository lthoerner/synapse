package com.lthoerner.synapse.mixin.jukebox;

import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Items.class)
public abstract class ItemsMixin {
    /*
    The following two methods remap the vanilla signal strength values of various music discs. The mapping goes as
    follows, and the altered discs have been annotated appropriately.

    0: far (from 5)
    1: otherside (from 14)
    2: cat
    3: blocks
    4: chirp
    5: 5 (from 15)
    6: mall
    7: mellohi
    8: stal
    9: strad
    10: ward
    11: 11
    12: wait
    13: 13 (from 1)
    14: relic
    15: pigstep (from 13)
     */

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=music_disc_13"),
                    to = @At(value = "CONSTANT", args = "stringValue=music_disc_relic")
            ),
            at = @At(value = "INVOKE", target = "net/minecraft/item/MusicDiscItem.<init> (ILnet/minecraft/sound/SoundEvent;Lnet/minecraft/item/Item$Settings;I)V"),
            index = 0
    )
    private static int editMusicDiscSignalStrength1(int original) {
        switch (original) {
            case 1: return 13;
            case 5: return 0;
            case 14: return 1;
            default: return original;
        }
    }

    @ModifyArg(
            method = "<clinit>",
            slice = @Slice(
                    from = @At(value = "CONSTANT", args = "stringValue=music_disc_5"),
                    to = @At(value = "CONSTANT", args = "stringValue=disc_fragment_5")
            ),
            at = @At(value = "INVOKE", target = "net/minecraft/item/MusicDiscItem.<init> (ILnet/minecraft/sound/SoundEvent;Lnet/minecraft/item/Item$Settings;I)V"),
            index = 0
    )
    private static int editMusicDiscSignalStrength2(int original) {
        switch (original) {
            case 13: return 15;
            case 15: return 5;
            default: return original;
        }
    }
}
