package com.handlock_.flattersigns.client;

import com.handlock_.flattersigns.FlatterSigns;
import com.handlock_.flattersigns.FlatterSignsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.HangingSignBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class FlatterSignsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Make sure the client also has the config available.
        FlatterSignsConfig.load();

        // Receive "force rerender" packets (server tells us to rebuild the sign's chunk mesh now).
        ClientPlayNetworking.registerGlobalReceiver(FlatterSigns.FORCE_SIGN_RERENDER_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    final BlockPos pos = buf.readBlockPos();

                    client.execute(() -> {
                        if (!FlatterSignsConfig.isFlatModelRenderingEnabled()
                                || !FlatterSignsConfig.isGlowInkLightingEnabled()) {
                            return;
                        }

                        if (client.world == null) {
                            return;
                        }

                        BlockState state = client.world.getBlockState(pos);
                        // Force a rerender: oldState must differ from newState or some paths no-op.
                        client.world.scheduleBlockRerenderIfNeeded(pos, Blocks.AIR.getDefaultState(), state);
                    });
                });

        var map = BlockRenderLayerMap.INSTANCE;
        var cutout = RenderLayer.getCutout();

        // Standing & wall signs.
        map.putBlocks(cutout,
                Blocks.OAK_SIGN, Blocks.OAK_WALL_SIGN,
                Blocks.SPRUCE_SIGN, Blocks.SPRUCE_WALL_SIGN,
                Blocks.BIRCH_SIGN, Blocks.BIRCH_WALL_SIGN,
                Blocks.JUNGLE_SIGN, Blocks.JUNGLE_WALL_SIGN,
                Blocks.ACACIA_SIGN, Blocks.ACACIA_WALL_SIGN,
                Blocks.DARK_OAK_SIGN, Blocks.DARK_OAK_WALL_SIGN,
                Blocks.MANGROVE_SIGN, Blocks.MANGROVE_WALL_SIGN,
                Blocks.CHERRY_SIGN, Blocks.CHERRY_WALL_SIGN,
                Blocks.BAMBOO_SIGN, Blocks.BAMBOO_WALL_SIGN,
                Blocks.CRIMSON_SIGN, Blocks.CRIMSON_WALL_SIGN,
                Blocks.WARPED_SIGN, Blocks.WARPED_WALL_SIGN
        );

        // Hanging signs (ceiling & wall).
        map.putBlocks(cutout,
                Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN,
                Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN,
                Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN,
                Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN,
                Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN,
                Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN,
                Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN,
                Blocks.CHERRY_HANGING_SIGN, Blocks.CHERRY_WALL_HANGING_SIGN,
                Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN,
                Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN,
                Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN
        );

        // Replace vanilla sign renderers with no-op versions only when
        // flat model rendering is enabled. When disabled, vanilla renderers stay.
        if (FlatterSignsConfig.isFlatModelRenderingEnabled()) {
            BlockEntityRendererFactories.register(BlockEntityType.SIGN, BlankSignRenderer::new);
            BlockEntityRendererFactories.register(BlockEntityType.HANGING_SIGN, BlankHangingSignRenderer::new);
        }
    }

    // Normal standing / wall signs: do not render text.
    private static class BlankSignRenderer implements BlockEntityRenderer<SignBlockEntity> {
        public BlankSignRenderer(BlockEntityRendererFactory.Context ctx) {}

        @Override
        public void render(SignBlockEntity entity,
                           float tickDelta,
                           MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers,
                           int light,
                           int overlay) {
            // Intentionally empty: blocks still render via models, but no text is drawn.
        }
    }

    // Hanging signs: also do not render text.
    private static class BlankHangingSignRenderer implements BlockEntityRenderer<HangingSignBlockEntity> {
        public BlankHangingSignRenderer(BlockEntityRendererFactory.Context ctx) {}

        @Override
        public void render(HangingSignBlockEntity entity,
                           float tickDelta,
                           MatrixStack matrices,
                           VertexConsumerProvider vertexConsumers,
                           int light,
                           int overlay) {
            // Also intentionally empty.
        }
    }
}
