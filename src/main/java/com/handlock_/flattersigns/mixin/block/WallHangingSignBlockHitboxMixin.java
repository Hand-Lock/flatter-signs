package com.handlock_.flattersigns.mixin.block;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallHangingSignBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Vanilla's bar, with the board 2px shorter at the bottom and 1px higher, like the plate model. */
@Mixin(WallHangingSignBlock.class)
public abstract class WallHangingSignBlockHitboxMixin {
    private static final VoxelShape flattersigns$NORTH_SOUTH = VoxelShapes.union(
            Block.createCuboidShape(0, 14, 6, 16, 16, 10),
            Block.createCuboidShape(1, 2, 7, 15, 11, 9));
    private static final VoxelShape flattersigns$EAST_WEST = VoxelShapes.union(
            Block.createCuboidShape(6, 14, 0, 10, 16, 16),
            Block.createCuboidShape(7, 2, 1, 9, 11, 15));

    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    private void flattersigns$shape(BlockState state, BlockView world, BlockPos pos, ShapeContext context,
                                    CallbackInfoReturnable<VoxelShape> cir) {
        if (!FlatterSignsConfig.isHitboxTweaksEnabled() || !FlatterSignsConfig.isFlatModelRenderingEnabled()) {
            return;
        }
        cir.setReturnValue(state.get(WallHangingSignBlock.FACING).getAxis() == Direction.Axis.X
                ? flattersigns$EAST_WEST : flattersigns$NORTH_SOUTH);
    }
}
