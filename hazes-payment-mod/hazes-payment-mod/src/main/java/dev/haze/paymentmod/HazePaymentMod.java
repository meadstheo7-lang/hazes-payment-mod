package dev.haze.paymentmod;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HazePaymentMod implements ClientModInitializer {
    private static Pattern payPattern;

    @Override
    public void onInitializeClient() {
        HazeConfig.load();
        payPattern = Pattern.compile(HazeConfig.INSTANCE.payRegex, Pattern.CASE_INSENSITIVE);

        // HUD overlay
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("hazespaymentmod", "pot_hud"),
                (graphics, tickCounter) -> HazeHud.draw(graphics, false));

        // Detect incoming payments
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (overlay) return;
            Matcher m = payPattern.matcher(message.getString());
            if (!m.find()) return;
            double amount = BetTracker.parseAmount(m.group(2));
            if (amount <= 0) return;
            BetTracker.add(m.group(1), amount);
            say(Component.literal("[Haze] ").withStyle(ChatFormatting.GOLD).append(
                    Component.literal(m.group(1) + " bet " + BetTracker.fmt(amount)
                            + " (total " + BetTracker.fmt(BetTracker.BETS.get(m.group(1))) + ")")
                            .withStyle(ChatFormatting.YELLOW)));
        });

        // Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("bets")
                        .executes(ctx -> { list(); return 1; })
                        .then(ClientCommandManager.literal("config").executes(ctx -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> mc.setScreen(new HazeConfigScreen(null))); // next tick so chat doesn't close it
                            return 1;
                        }))
                        .then(ClientCommandManager.literal("clear").executes(ctx -> {
                            BetTracker.clear(); say(Component.literal("[Haze] Cleared.")); return 1;
                        }))
                        .then(ClientCommandManager.literal("win")
                                .then(ClientCommandManager.argument("player", StringArgumentType.word())
                                        .executes(ctx -> { payout(StringArgumentType.getString(ctx, "player"),
                                                HazeConfig.INSTANCE.defaultMultiplier); return 1; })
                                        .then(ClientCommandManager.argument("multiplier", DoubleArgumentType.doubleArg(0))
                                                .executes(ctx -> { payout(StringArgumentType.getString(ctx, "player"),
                                                        DoubleArgumentType.getDouble(ctx, "multiplier")); return 1; }))))));
    }

    private void list() {
        if (BetTracker.BETS.isEmpty()) { say(Component.literal("[Haze] No bets yet.")); return; }
        double mult = HazeConfig.INSTANCE.defaultMultiplier;
        for (var e : BetTracker.BETS.entrySet()) {
            say(Component.literal(e.getKey() + ": " + BetTracker.fmt(e.getValue()) + "  ")
                    .append(button("[PAY " + mult + "x]", e.getKey(), e.getValue() * mult))
                    .append(Component.literal(" "))
                    .append(button("[REFUND]", e.getKey(), e.getValue())));
        }
        say(Component.literal("Total pot: " + BetTracker.fmt(BetTracker.pot())).withStyle(ChatFormatting.GREEN));
    }

    private void payout(String player, double mult) {
        Double bet = BetTracker.BETS.get(player);
        if (bet == null) { say(Component.literal("[Haze] No bet found for " + player)); return; }
        say(Component.literal("Payout for " + player + ": ")
                .append(button("[PAY " + BetTracker.fmt(bet * mult) + "]", player, bet * mult)));
    }

    /** Pre-fills /pay in your chat box; you press Enter. Nothing is ever sent automatically. */
    private Component button(String label, String player, double amount) {
        String cmd = "/pay " + player + " " + (long) amount;
        return Component.literal(label).withStyle(s -> s.withColor(ChatFormatting.GREEN).withBold(true)
                .withClickEvent(new ClickEvent.SuggestCommand(cmd)));
    }

    private static void say(Component c) { Minecraft.getInstance().gui.getChat().addMessage(c); }
}
