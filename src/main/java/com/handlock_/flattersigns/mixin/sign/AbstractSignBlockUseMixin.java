package com.handlock_.flattersigns.mixin.sign;

import com.handlock_.flattersigns.FlatterSignsConfig;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Right-click reads the sign in chat; crouching (or an empty sign) edits it. */
@Mixin(AbstractSignBlock.class)
public abstract class AbstractSignBlockUseMixin {

    // Only reached on the server, after vanilla has handled dye, ink and wax.
    @Redirect(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/AbstractSignBlock;openEditScreen(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/block/entity/SignBlockEntity;Z)V"
            )
    )
    private void flattersigns$readOrEdit(AbstractSignBlock instance, PlayerEntity player,
                                         SignBlockEntity sign, boolean front) {
        SignText text = sign.getText(front);
        if (flattersigns$reads(player, text)) {
            flattersigns$sendToChat(player, text);
        } else {
            instance.openEditScreen(player, sign, front);
        }
    }

    // The waxed "can't edit" sound only makes sense on an edit attempt.
    @Redirect(
            method = "onUse",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;)V"
            )
    )
    private void flattersigns$waxedSound(World instance, PlayerEntity except, BlockPos soundPos, SoundEvent sound,
                                         SoundCategory category, BlockState state, World world, BlockPos pos,
                                         PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof SignBlockEntity sign)
                || !flattersigns$reads(player, sign.getTextFacing(player))) {
            instance.playSound(except, soundPos, sound, category);
        }
    }

    // Vanilla returns early for waxed signs; checked at HEAD so the wax just
    // being applied doesn't count.
    @Inject(method = "onUse", at = @At("HEAD"))
    private void flattersigns$readWaxed(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                        Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!FlatterSignsConfig.isCrouchEditAndChatEnabled()) {
            return;
        }
        if (!world.isClient && world.getBlockEntity(pos) instanceof SignBlockEntity sign && sign.isWaxed()) {
            SignText text = sign.getTextFacing(player);
            if (flattersigns$reads(player, text)) {
                flattersigns$sendToChat(player, text);
            }
        }
    }

    private static boolean flattersigns$reads(PlayerEntity player, SignText text) {
        return FlatterSignsConfig.isCrouchEditAndChatEnabled() && !player.isSneaking() && text.hasText(player);
    }

    private static void flattersigns$sendToChat(PlayerEntity player, SignText text) {
        MutableText content = Text.empty();
        boolean any = false;
        for (Text line : text.getMessages(player.shouldFilterText())) {
            if (line.getString().isBlank()) {
                continue;
            }
            if (any) {
                content.append(Text.literal(" | ").formatted(Formatting.DARK_GRAY));
            }
            content.append(line);
            any = true;
        }
        if (!any) {
            return;
        }
        content.styled(style -> style.withColor(text.getColor().getSignColor()));
        player.sendMessage(Text.literal("<Sign> ").formatted(Formatting.WHITE).append(content), false);
    }
}
