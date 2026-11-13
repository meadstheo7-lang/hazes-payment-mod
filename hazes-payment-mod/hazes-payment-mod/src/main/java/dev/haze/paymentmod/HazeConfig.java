package dev.haze.paymentmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

/** Saved to config/hazespaymentmod.json. Everything here is editable in-game via /bets config. */
public class HazeConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("hazespaymentmod.json");
    public static HazeConfig INSTANCE = new HazeConfig();

    public boolean hudEnabled = true;
    public int hudX = 2;            // % of screen width (0-100)
    public int hudY = 20;           // % of screen height (0-100)
    public int scalePercent = 100;  // 50-300
    public int bgOpacity = 50;      // 0-100
    public int textColor = 0xFFFFFF;
    public int accentColor = 0xFFD700;
    public int recentBetsShown = 5; // 0-10
    public double defaultMultiplier = 2.0;
    public String title = "Haze's Bets";

    /** Edit in the JSON if DonutSMP's message format differs. Group 1 = sender, group 2 = amount. */
    public String payRegex = "^(\\w{3,16}) paid you \\$?([\\d,.]+\\s?[kKmMbB]?)";

    public static void load() {
        try {
            if (Files.exists(FILE)) INSTANCE = GSON.fromJson(Files.readString(FILE), HazeConfig.class);
        } catch (Exception e) { INSTANCE = new HazeConfig(); }
        save();
    }

    public static void save() {
        try { Files.writeString(FILE, GSON.toJson(INSTANCE)); } catch (Exception ignored) {}
    }
}
