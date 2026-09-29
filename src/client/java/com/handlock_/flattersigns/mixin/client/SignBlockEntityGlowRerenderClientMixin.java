package com.handlock_.flattersigns.mixin.client;

import com.handlock_.flattersigns.FlatterSignsConfig;
import com.handlock_.flattersigns.client.FlatterSignsClient;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Glow is baked into the block model's light, so the chunk must be rebuilt
 * when it changes. Update packets land in readNbt on Fabric, and in load or
 * handleUpdateTag on Forge via Connector (ADR 0002); CompoundTag can't be
 * named at compile time, hence @Coerce and remap = false.
 */
@Mixin(SignBlockEntity.class)
public abstract class SignBlockEntityGlowRerenderClientMixin {

    @Unique
    private boolean flattersigns$lastGlowState = false;

    @Inject(method = "readNbt(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"), require = 0)
    private void flattersigns$readNbt(NbtCompound nbt, CallbackInfo ci) {
        flattersigns$rerenderOnGlowChange();
    }

    @Inject(method = "load(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), remap = false, require = 0)
    private void flattersigns$load(@Coerce Object tag, CallbackInfo ci) {
        flattersigns$rerenderOnGlowChange();
    }

    @Inject(method = "handleUpdateTag(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("TAIL"), remap = false, require = 0)
    private void flattersigns$handleUpdateTag(@Coerce Object tag, CallbackInfo ci) {
        flattersigns$rerenderOnGlowChange();
    }

    @Unique
    private void flattersigns$rerenderOnGlowChange() {
        if (!FlatterSignsConfig.isFlatModelRenderingEnabled() || !FlatterSignsConfig.isGlowInkLightingEnabled()) {
            return;
        }
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        // Null while a chunk is still being loaded.
        if (!(sign.getWorld() instanceof ClientWorld world)) {
            return;
        }
        boolean glowing = sign.getFrontText().isGlowing() || sign.getBackText().isGlowing();
        if (glowing != flattersigns$lastGlowState) {
            flattersigns$lastGlowState = glowing;
            FlatterSignsClient.rerender(world, sign.getPos());
        }
    }
}
