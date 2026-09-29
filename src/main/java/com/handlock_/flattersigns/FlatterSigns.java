package com.handlock_.flattersigns;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatterSigns implements ModInitializer {
    public static final String MOD_ID = "flattersigns";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // S2C, carries a BlockPos. On Forge via Connector a glow change doesn't
    // always rebuild the chunk, so the server asks for it explicitly (ADR 0002).
    public static final Identifier FORCE_SIGN_RERENDER_PACKET_ID =
            new Identifier(MOD_ID, "force_sign_rerender");

    @Override
    public void onInitialize() {
        FlatterSignsConfig.load();
    }
}
