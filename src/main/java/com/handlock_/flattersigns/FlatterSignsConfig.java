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

/** config/flattersigns.json: flat, primitives only, so it is easy to edit by hand (ADR 0005). */
public final class FlatterSignsConfig {

    private static final String FILE_NAME = "flattersigns.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static FlatterSignsConfig INSTANCE;

    public boolean frontOnlyEdit = true;
    public boolean flatModelRendering = true;
    public boolean hitboxTweaks = true;
    public boolean crouchEditAndChat = true;
    public boolean defaultWhiteText = true;
    public boolean glowInkLighting = true;
    // A window into the sign item texture; 11/0 trims the vanilla stem.
    public int wallSignTextureCropOffset = 0;
    public int wallSignTextureCropHeight = 11;

    public static void load() {
        if (INSTANCE != null) {
            return;
        }

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
                cfg.wallSignTextureCropHeight = getInt(obj, "wall_sign_texture_crop_height",
                        cfg.wallSignTextureCropHeight, 1, 16);
                cfg.wallSignTextureCropOffset = getInt(obj, "wall_sign_texture_crop_offset",
                        cfg.wallSignTextureCropOffset, 0, 16 - cfg.wallSignTextureCropHeight);
            } catch (Exception e) {
                FlatterSigns.LOGGER.warn("Failed to read config file '{}'. Using defaults and rewriting it.", path, e);
            }
        }

        INSTANCE = cfg;

        // Rewrite every time so new keys show up after an update.
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

    public static int getWallSignTextureCropOffset() {
        return get().wallSignTextureCropOffset;
    }

    public static int getWallSignTextureCropHeight() {
        return get().wallSignTextureCropHeight;
    }

    private static FlatterSignsConfig get() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    private static Path getPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean def) {
        try {
            if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                return obj.get(key).getAsBoolean();
            }
        } catch (Exception ignored) {}
        return def;
    }

    private static int getInt(JsonObject obj, String key, int def, int min, int max) {
        int v = def;
        try {
            if (obj.has(key) && obj.get(key).isJsonPrimitive()) {
                v = obj.get(key).getAsInt();
            }
        } catch (Exception ignored) {}
        return Math.max(min, Math.min(max, v));
    }

    private static void write(FlatterSignsConfig cfg) throws IOException {
        Path path = getPath();
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(toJson(cfg), writer);
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
        obj.addProperty("wall_sign_texture_crop_offset", cfg.wallSignTextureCropOffset);
        obj.addProperty("wall_sign_texture_crop_height", cfg.wallSignTextureCropHeight);
        return obj;
    }
}
