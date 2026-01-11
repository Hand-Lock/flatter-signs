package com.handlock_.flattersigns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Simple JSON config that lives in {@code config/flattersigns.json}.
 *
 * <p>The file is always written back on load to ensure all keys exist and are up-to-date.</p>
 *
 * <p>Note: config is loaded once at startup; changes require a restart.</p>
 */
public final class FlatterSignsConfig {

    private static final String FILE_NAME = "flattersigns.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static boolean loaded = false;
    private static FlatterSignsConfig INSTANCE;

    // Toggles -------------------------------------------------------------
    /** Front-only edit / read behaviour for signs. */
    public boolean frontOnlyEdit = true;

    /**
     * Use flat cross / plate models and blank BER, instead of
     * vanilla entity rendering.
     */
    public boolean flatModelRendering = true;

    /** Hitbox / outline tweaks for signs and hanging signs. */
    public boolean hitboxTweaks = true;

    /**
     * Only edit when crouching (or when empty) and print text to chat on
     * normal right-click. This toggle controls both behaviours together.
     */
    public boolean crouchEditAndChat = true;

    /** Make the default sign text color white instead of black. */
    public boolean defaultWhiteText = true;

    /**
     * Make glow ink sac actually emit block light (server-side) and force a
     * block update so it is visible immediately (especially on Forge+Sinytra).
     */
    public boolean glowInkLighting = true;

    private FlatterSignsConfig() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;

        FlatterSignsConfig cfg = new FlatterSignsConfig();
        Path path = getPath();

        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();

                cfg.frontOnlyEdit = getBoolean(obj, "front_only_edit", cfg.frontOnlyEdit);
                cfg.flatModelRendering = getBoolean(obj, "flat_model_rendering", cfg.flatModelRendering);
                cfg.hitboxTweaks = getBoolean(obj, "hitbox_tweaks", cfg.hitboxTweaks);
                cfg.crouchEditAndChat = getBoolean(obj, "crouch_edit_and_chat", cfg.crouchEditAndChat);
                cfg.defaultWhiteText = getBoolean(obj, "default_white_text", cfg.defaultWhiteText);
                cfg.glowInkLighting = getBoolean(obj, "glow_ink_lighting", cfg.glowInkLighting);
            } catch (Exception e) {
                // If the file is malformed, keep defaults and overwrite it.
                FlatterSigns.LOGGER.warn("Failed to read config file '{}'. Using defaults and rewriting it.", path, e);
            }
        }

        INSTANCE = cfg;

        // Always write back so the file exists and includes all keys.
        try {
            write(cfg);
        } catch (IOException e) {
            FlatterSigns.LOGGER.warn("Failed to write config file '{}'.", path, e);
        }
    }

    public static boolean isFrontOnlyEditEnabled() {
        return get().frontOnlyEdit;
    }

    public static boolean isFlatModelRenderingEnabled() {
        return get().flatModelRendering;
    }

    public static boolean isHitboxTweaksEnabled() {
        return get().hitboxTweaks;
    }

    public static boolean isCrouchEditAndChatEnabled() {
        return get().crouchEditAndChat;
    }

    public static boolean isDefaultWhiteTextEnabled() {
        return get().defaultWhiteText;
    }

    public static boolean isGlowInkLightingEnabled() {
        return get().glowInkLighting;
    }

    // Internal helpers ---------------------------------

    private static FlatterSignsConfig get() {
        if (!loaded) {
            load();
        }
        return INSTANCE;
    }

    private static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean def) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return def;
        }

        try {
            return obj.get(key).getAsBoolean();
        } catch (Exception e) {
            return def;
        }
    }

    private static void write(FlatterSignsConfig cfg) throws IOException {
        Path path = getPath();
        Files.createDirectories(path.getParent());

        JsonObject obj = toJson(cfg);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(obj, writer);
        }
    }

    private static JsonObject toJson(FlatterSignsConfig cfg) {
        JsonObject obj = new JsonObject();
        obj.addProperty("front_only_edit", cfg.frontOnlyEdit);
        obj.addProperty("flat_model_rendering", cfg.flatModelRendering);
        obj.addProperty("hitbox_tweaks", cfg.hitboxTweaks);
        obj.addProperty("crouch_edit_and_chat", cfg.crouchEditAndChat);
        obj.addProperty("default_white_text", cfg.defaultWhiteText);
        obj.addProperty("glow_ink_lighting", cfg.glowInkLighting);
        return obj;
    }
}
