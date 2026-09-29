package com.handlock_.flattersigns.mixin.sign;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * One-sided signs: every read and write goes to the front. setText and
 * getTextFacing route through these, so they need no hooks of their own.
 */
@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityFrontOnlyMixin {
    @Shadow public abstract SignText getFrontText();
    @Shadow protected abstract boolean setFrontText(SignText frontText);

    @Inject(method = "getText(Z)Lnet/minecraft/block/entity/SignText;", at = @At("HEAD"), cancellable = true)
    private void flattersigns$forceFrontGet(boolean front, CallbackInfoReturnable<SignText> cir) {
        if (!FlatterSignsConfig.isFrontOnlyEditEnabled()) {
            return;
        }
        cir.setReturnValue(this.getFrontText());
    }

    @Inject(method = "setBackText(Lnet/minecraft/block/entity/SignText;)Z", at = @At("HEAD"), cancellable = true)
    private void flattersigns$redirectBackToFront(SignText backText, CallbackInfoReturnable<Boolean> cir) {
        if (!FlatterSignsConfig.isFrontOnlyEditEnabled()) {
            return;
        }
        cir.setReturnValue(this.setFrontText(backText));
    }

    @Inject(method = "isPlayerFacingFront(Lnet/minecraft/entity/player/PlayerEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void flattersigns$alwaysFront(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (!FlatterSignsConfig.isFrontOnlyEditEnabled()) {
            return;
        }
        cir.setReturnValue(true);
    }
}
