package dev.haze.paymentmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class HazeConfigScreen extends Screen {
    private static final int[] COLORS = {0xFFFFFF, 0xFFD700, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xAAAAAA};
    private static final String[] COLOR_NAMES = {"White", "Gold", "Green", "Aqua", "Red", "Pink", "Gray"};

    private final Screen parent;

    public HazeConfigScreen(Screen parent) {
        super(Component.literal("Haze's Payment Mod"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        HazeConfig c = HazeConfig.INSTANCE;
        int w = 150, h = 20, left = 10, top = 30, gap = 24;

        addRenderableWidget(Button.builder(Component.literal("HUD: " + (c.hudEnabled ? "ON" : "OFF")), b -> {
            c.hudEnabled = !c.hudEnabled;
            b.setMessage(Component.literal("HUD: " + (c.hudEnabled ? "ON" : "OFF")));
        }).bounds(left, top, w, h).build());

        addRenderableWidget(slider(left, top + gap, w, h, "Position X", 0, 100, c.hudX, v -> c.hudX = v, "%"));
        addRenderableWidget(slider(left, top + gap * 2, w, h, "Position Y", 0, 100, c.hudY, v -> c.hudY = v, "%"));
        addRenderableWidget(slider(left, top + gap * 3, w, h, "Scale", 50, 300, c.scalePercent, v -> c.scalePercent = v, "%"));
        addRenderableWidget(slider(left, top + gap * 4, w, h, "Background", 0, 100, c.bgOpacity, v -> c.bgOpacity = v, "%"));
        addRenderableWidget(slider(left, top + gap * 5, w, h, "Recent bets", 0, 10, c.recentBetsShown, v -> c.recentBetsShown = v, ""));

        addRenderableWidget(colorButton(left, top + gap * 6, w, h, "Text", c.textColor, v -> c.textColor = v));
        addRenderableWidget(colorButton(left, top + gap * 7, w, h, "Accent", c.accentColor, v -> c.accentColor = v));

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(left, top + gap * 8 + 6, w, h).build());
    }

    private Button colorButton(int x, int y, int w, int h, String label, int current, IntConsumer setter) {
        int[] idx = {0};
        for (int i = 0; i < COLORS.length; i++) if (COLORS[i] == current) idx[0] = i;
        return Button.builder(Component.literal(label + ": " + COLOR_NAMES[idx[0]]), b -> {
            idx[0] = (idx[0] + 1) % COLORS.length;
            setter.accept(COLORS[idx[0]]);
            b.setMessage(Component.literal(label + ": " + COLOR_NAMES[idx[0]]));
        }).bounds(x, y, w, h).build();
    }

    private AbstractSliderButton slider(int x, int y, int w, int h, String label, int min, int max, int value,
                                        IntConsumer setter, String suffix) {
        return new AbstractSliderButton(x, y, w, h, Component.literal(label + ": " + value + suffix),
                (value - min) / (double) (max - min)) {
            private int current() { return min + (int) Math.round(this.value * (max - min)); }
            @Override protected void updateMessage() { setMessage(Component.literal(label + ": " + current() + suffix)); }
            @Override protected void applyValue() { setter.accept(current()); }
        };
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        g.drawString(this.font, "Haze's Payment Mod - HUD settings (live preview)", 10, 12, 0xFFFFFFFF, true);
        HazeHud.draw(g, true); // preview follows your settings as you drag the sliders
    }

    @Override
    public void onClose() {
        HazeConfig.save();
        Minecraft.getInstance().setScreen(parent);
    }
}
