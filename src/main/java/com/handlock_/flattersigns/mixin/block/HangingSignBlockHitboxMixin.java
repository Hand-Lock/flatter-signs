package com.handlock_.flattersigns.mixin.block;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HangingSignBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The cross model looks the same at every rotation, so the outline does too. */
@Mixin(HangingSignBlock.class)
public abstract class HangingSignBlockHitboxMixin {
    private static final VoxelShape flattersigns$SHAPE = Block.createCuboidShape(4, 2, 4, 12, 11, 12);

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void flattersigns$shape(BlockState state, BlockView world, BlockPos pos, ShapeContext context,
                                    CallbackInfoReturnable<VoxelShape> cir) {
        if (!FlatterSignsConfig.isHitboxTweaksEnabled() || !FlatterSignsConfig.isFlatModelRenderingEnabled()) {
            return;
        }
        cir.setReturnValue(flattersigns$SHAPE);
    }
}
