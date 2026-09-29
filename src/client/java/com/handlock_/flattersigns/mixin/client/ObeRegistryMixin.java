package com.handlock_.flattersigns.mixin.client;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Optimized Block Entities bakes the sign entity model into chunk meshes, on
 * top of our flat models. Its own sign toggles are miswired, so signs opt out
 * here (ADR 0008).
 */
@Pseudo
@Mixin(targets = "fr.madu59.obe.client.registry.Registry", remap = false)
public abstract class ObeRegistryMixin {

    @Inject(
            method = "isSupported(Ljava/lang/String;Lnet/minecraft/class_2591;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void flattersigns$skipSignTypes(String id, BlockEntityType<?> type,
                                                   CallbackInfoReturnable<Boolean> cir) {
        if (FlatterSignsConfig.isFlatModelRenderingEnabled()
                && (type == BlockEntityType.SIGN || type == BlockEntityType.HANGING_SIGN)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "getGroup(Lnet/minecraft/class_2680;)Ljava/lang/String;",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void flattersigns$skipSignStates(BlockState state, CallbackInfoReturnable<String> cir) {
        if (FlatterSignsConfig.isFlatModelRenderingEnabled() && state.getBlock() instanceof AbstractSignBlock) {
            cir.setReturnValue(null);
        }
    }
}
