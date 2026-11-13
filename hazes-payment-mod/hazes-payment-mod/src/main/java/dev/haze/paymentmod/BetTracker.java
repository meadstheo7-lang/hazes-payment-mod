package dev.haze.paymentmod;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class BetTracker {
    public static final Map<String, Double> BETS = new LinkedHashMap<>();
    public static final Deque<String> RECENT = new ArrayDeque<>();

    public static void add(String player, double amount) {
        BETS.merge(player, amount, Double::sum);
        RECENT.addFirst(player + " " + fmt(amount));
        while (RECENT.size() > 10) RECENT.removeLast();
    }

    public static void clear() { BETS.clear(); RECENT.clear(); }

    public static double pot() { return BETS.values().stream().mapToDouble(Double::doubleValue).sum(); }

    public static double parseAmount(String raw) {
        String s = raw.replace(",", "").trim().toLowerCase(Locale.ROOT);
        double mult = 1;
        if (s.endsWith("k")) { mult = 1e3; s = s.substring(0, s.length() - 1); }
        else if (s.endsWith("m")) { mult = 1e6; s = s.substring(0, s.length() - 1); }
        else if (s.endsWith("b")) { mult = 1e9; s = s.substring(0, s.length() - 1); }
        try { return Double.parseDouble(s.trim()) * mult; } catch (NumberFormatException e) { return -1; }
    }

    public static String fmt(double v) {
        if (v >= 1e9) return String.format("$%.2fB", v / 1e9);
        if (v >= 1e6) return String.format("$%.2fM", v / 1e6);
        if (v >= 1e3) return String.format("$%.1fK", v / 1e3);
        return "$" + (long) v;
    }
}
