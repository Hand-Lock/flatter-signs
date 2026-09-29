package com.handlock_.flattersigns.mixin.sign;

import com.handlock_.flattersigns.FlatterSigns;
import com.handlock_.flattersigns.FlatterSignsConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlowInkSacItem;
import net.minecraft.item.InkSacItem;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

/**
 * On Forge via Connector a glow change doesn't always reach the client or
 * rebuild the chunk (ADR 0002). Hooked here rather than in onUse because this
 * is exactly when glow changes, and the last ink sac hasn't been consumed yet.
 */
@Mixin({GlowInkSacItem.class, InkSacItem.class})
public abstract class InkSacItemGlowMixin {

    @Inject(method = "useOnSign", at = @At("RETURN"))
    private void flattersigns$syncGlow(World world, SignBlockEntity sign, boolean front, PlayerEntity player,
                                       CallbackInfoReturnable<Boolean> cir) {
        if (!FlatterSignsConfig.isFlatModelRenderingEnabled() || !FlatterSignsConfig.isGlowInkLightingEnabled()) {
            return;
        }
        if (!cir.getReturnValueZ() || !(world instanceof ServerWorld)) {
            return;
        }

        // Tracking may be incomplete on Connector; the acting player always needs it.
        Set<ServerPlayerEntity> targets = new HashSet<>(PlayerLookup.tracking(sign));
        if (player instanceof ServerPlayerEntity sp) {
            targets.add(sp);
        }

        BlockEntityUpdateS2CPacket update = BlockEntityUpdateS2CPacket.create(sign);
        for (ServerPlayerEntity p : targets) {
            p.networkHandler.sendPacket(update);
            var buf = PacketByteBufs.create();
            buf.writeBlockPos(sign.getPos());
            ServerPlayNetworking.send(p, FlatterSigns.FORCE_SIGN_RERENDER_PACKET_ID, buf);
        }
    }
}
