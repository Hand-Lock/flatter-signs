package com.handlock_.flattersigns.client;

import com.handlock_.flattersigns.FlatterSigns;
import com.handlock_.flattersigns.FlatterSignsConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlatterSignsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        if (FlatterSignsConfig.isFlatModelRenderingEnabled()) {
            // Every sign, modded ones included, so a resource pack can give them flat models (ADR 0007).
            for (Block block : Registries.BLOCK) {
                if (block instanceof AbstractSignBlock) {
                    BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
                }
            }

            // The model draws the sign; the text goes to chat.
            BlockEntityRendererFactory<SignBlockEntity> blank = ctx -> (sign, tickDelta, matrices, vertices, light, overlay) -> {};
            BlockEntityRendererFactories.register(BlockEntityType.SIGN, blank);
            BlockEntityRendererFactories.register(BlockEntityType.HANGING_SIGN, blank);

            registerWallSignCrop();
        }

        //? if >=1.20.5 {
        // Fabric already runs payload handlers on the client thread.
        ClientPlayNetworking.registerGlobalReceiver(FlatterSigns.ForceSignRerender.ID, (payload, context) -> {
            if (context.client().world != null) {
                rerender(context.client().world, payload.pos());
            }
        });
        //?} else {
        /*ClientPlayNetworking.registerGlobalReceiver(FlatterSigns.FORCE_SIGN_RERENDER_PACKET_ID,
                (client, handler, buf, responseSender) -> {
                    BlockPos pos = buf.readBlockPos();
                    client.execute(() -> {
                        if (client.world != null) {
                            rerender(client.world, pos);
                        }
                    });
                });
        *///?}
    }

    public static void rerender(World world, BlockPos pos) {
        // A different old state, or the rebuild is skipped.
        world.scheduleBlockRerenderIfNeeded(pos, Blocks.AIR.getDefaultState(), world.getBlockState(pos));
    }

    // Done on load rather than in the generator so it follows the config and
    // applies to resource-pack overrides of our models too.
    private static void registerWallSignCrop() {
        int h = FlatterSignsConfig.getWallSignTextureCropHeight();
        int o = FlatterSignsConfig.getWallSignTextureCropOffset();
        ModelLoadingPlugin.register(ctx -> ctx.modifyModelOnLoad().register((model, context) -> {
            // Null on 1.21+ for models that don't come from a resource.
            //? if >=1.21 {
            Identifier id = context.resourceId();
            //?} else {
            /*Identifier id = context.id();
            *///?}
            if (id != null && id.getNamespace().equals(FlatterSigns.MOD_ID)
                    && id.getPath().contains("_wall_sign_flat_")
                    && model instanceof JsonUnbakedModel json) {
                for (ModelElement element : json.getElements()) {
                    element.from.y = element.to.y - h;
                    for (ModelElementFace face : element.faces.values()) {
                        //? if >=1.21 {
                        float[] uvs = face.textureData().uvs;
                        //?} else {
                        /*float[] uvs = face.textureData.uvs;
                        *///?}
                        uvs[1] = o;
                        uvs[3] = o + h;
                    }
                }
            }
            return model;
        }));
    }
}
