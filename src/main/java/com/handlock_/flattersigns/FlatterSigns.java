package com.handlock_.flattersigns;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
//? if >=1.20.5 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
//?}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatterSigns implements ModInitializer {
    public static final String MOD_ID = "flattersigns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // S2C, carries a BlockPos. On Forge via Connector a glow change doesn't
    // always rebuild the chunk, so the server asks for it explicitly (ADR 0002).
    //? if >=1.20.5 {
    public record ForceSignRerender(BlockPos pos) implements CustomPayload {
        public static final Id<ForceSignRerender> ID = new Id<>(Identifier.of(MOD_ID, "force_sign_rerender"));
        public static final PacketCodec<RegistryByteBuf, ForceSignRerender> CODEC =
                BlockPos.PACKET_CODEC.<RegistryByteBuf>cast().xmap(ForceSignRerender::new, ForceSignRerender::pos);

        @Override
        public Id<ForceSignRerender> getId() {
            return ID;
        }
    }
    //?} else {
    /*public static final Identifier FORCE_SIGN_RERENDER_PACKET_ID =
            new Identifier(MOD_ID, "force_sign_rerender");
    *///?}

    @Override
    public void onInitialize() {
        FlatterSignsConfig.load();
        //? if >=1.20.5
        PayloadTypeRegistry.playS2C().register(ForceSignRerender.ID, ForceSignRerender.CODEC);
    }
}
