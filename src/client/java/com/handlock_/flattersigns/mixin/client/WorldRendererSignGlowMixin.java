package com.handlock_.flattersigns.mixin.client;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Glowing signs render fullbright. Only the 3-arg overload is hooked: the
 * 2-arg one delegates to it. The descriptor is explicit so Mixin can't pick
 * the wrong overload, and the intermediary copy is for Connector (ADR 0002).
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererSignGlowMixin {

    @Inject(
            method = "getLightmapCoordinates(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)I",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void flattersigns$fullbright(BlockRenderView world, BlockState state, BlockPos pos,
                                                CallbackInfoReturnable<Integer> cir) {
        flattersigns$maybeFullbright(world, state, pos, cir);
    }

    @Inject(
            method = "method_23793(Lnet/minecraft/class_1920;Lnet/minecraft/class_2680;Lnet/minecraft/class_2338;)I",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private static void flattersigns$fullbrightIntermediary(BlockRenderView world, BlockState state, BlockPos pos,
                                                            CallbackInfoReturnable<Integer> cir) {
        flattersigns$maybeFullbright(world, state, pos, cir);
    }

    @Unique
    private static void flattersigns$maybeFullbright(BlockRenderView world, BlockState state, BlockPos pos,
                                                     CallbackInfoReturnable<Integer> cir) {
        if (!FlatterSignsConfig.isFlatModelRenderingEnabled() || !FlatterSignsConfig.isGlowInkLightingEnabled()) {
            return;
        }
        if (state.getBlock() instanceof AbstractSignBlock
                && world.getBlockEntity(pos) instanceof SignBlockEntity sign
                && (sign.getFrontText().isGlowing() || sign.getBackText().isGlowing())) {
            cir.setReturnValue(LightmapTextureManager.MAX_LIGHT_COORDINATE);
        }
    }
}
