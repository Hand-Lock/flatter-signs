package com.handlock_.flattersigns.mixin.client;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Glowing signs render fullbright. Emissive lighting is read by both vanilla's
 * getLightmapCoordinates and Sodium's lighting pipeline, which never calls
 * the former. The intermediary copy is for Connector (ADR 0002).
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateSignGlowMixin {

    @Shadow
    public abstract Block getBlock();

    @Inject(
            method = "hasEmissiveLighting(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)Z",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void flattersigns$emissive(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        flattersigns$maybeEmissive(world, pos, cir);
    }

    @Inject(
            method = "method_26208(Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void flattersigns$emissiveIntermediary(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        flattersigns$maybeEmissive(world, pos, cir);
    }

    // Hot path: cheap checks before the block entity lookup.
    @Unique
    private void flattersigns$maybeEmissive(BlockView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!FlatterSignsConfig.isFlatModelRenderingEnabled() || !FlatterSignsConfig.isGlowInkLightingEnabled()) {
            return;
        }
        if (getBlock() instanceof AbstractSignBlock
                && world.getBlockEntity(pos) instanceof SignBlockEntity sign
                && (sign.getFrontText().isGlowing() || sign.getBackText().isGlowing())) {
            cir.setReturnValue(true);
        }
    }
}
