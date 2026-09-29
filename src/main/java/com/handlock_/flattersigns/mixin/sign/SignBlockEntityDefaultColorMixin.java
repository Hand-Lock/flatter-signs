package com.handlock_.flattersigns.mixin.sign;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityDefaultColorMixin {

    @Inject(method = "createText", at = @At("RETURN"), cancellable = true)
    private void flattersigns$defaultWhiteText(CallbackInfoReturnable<SignText> cir) {
        if (!FlatterSignsConfig.isDefaultWhiteTextEnabled()) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue().withColor(DyeColor.WHITE));
    }
}
