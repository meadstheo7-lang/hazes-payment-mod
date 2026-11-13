package dev.haze.paymentmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

/** Draws the pot overlay. Also used as the live preview inside the config screen. */
public class HazeHud {
    public static void draw(GuiGraphics g, boolean preview) {
        HazeConfig c = HazeConfig.INSTANCE;
        if (!c.hudEnabled && !preview) return;

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        List<String> lines = new ArrayList<>();
        lines.add(c.title);
        lines.add("Pot: " + BetTracker.fmt(preview && BetTracker.BETS.isEmpty() ? 1_250_000 : BetTracker.pot()));
        lines.add("Players: " + (preview && BetTracker.BETS.isEmpty() ? 3 : BetTracker.BETS.size()));
        int shown = 0;
        for (String r : BetTracker.RECENT) { if (shown++ >= c.recentBetsShown) break; lines.add("  " + r); }
        if (preview && BetTracker.RECENT.isEmpty() && c.recentBetsShown > 0) {
            lines.add("  Steve $500.0K"); lines.add("  Alex $250.0K");
        }

        float scale = c.scalePercent / 100f;
        int width = 0;
        for (String l : lines) width = Math.max(width, font.width(l));
        int boxW = width + 8, boxH = lines.size() * 10 + 6;

        int sx = g.guiWidth(), sy = g.guiHeight();
        int x = (int) ((sx - boxW * scale) * c.hudX / 100f);
        int y = (int) ((sy - boxH * scale) * c.hudY / 100f);

        g.pose().pushMatrix();
        g.pose().translate(x, y);
        g.pose().scale(scale, scale);

        int alpha = (int) (c.bgOpacity / 100f * 255) << 24;
        g.fill(0, 0, boxW, boxH, alpha);
        for (int i = 0; i < lines.size(); i++) {
            int color = (i == 0 ? c.accentColor : c.textColor) | 0xFF000000;
            g.drawString(font, lines.get(i), 4, 4 + i * 10, color, true);
        }
        g.pose().popMatrix();
    }
}
