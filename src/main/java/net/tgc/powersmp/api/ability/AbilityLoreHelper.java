package net.tgc.powersmp.api.ability;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds consistent, formatted lore lines for any Ability.
 * Used by gem items and GemApplicationListener so the style is always identical.
 */
public final class AbilityLoreHelper {
    private AbilityLoreHelper() {}

    /**
     * Returns a lore block for one ability:
     *   ✦ Display Name
     *     Activation: Right-Click
     *     Power:      10 ⚡
     *     Cooldown:   2s
     */
    public static List<Component> build(Ability ability) {
        List<Component> lines = new ArrayList<>();

        lines.add(Component.text("  ✦ ", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(ability.getDisplayName(), NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false)));

        lines.add(row("Activation", formatTriggers(ability)));

        if (ability.getPowerCost() > 0) {
            String cost = ability.getPowerCost() == (long) ability.getPowerCost()
                    ? String.valueOf((long) ability.getPowerCost())
                    : String.valueOf(ability.getPowerCost());
            lines.add(Component.text("    Power:      ", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(cost + " ⚡", NamedTextColor.YELLOW)
                            .decoration(TextDecoration.ITALIC, false)));
        }

        if (ability.getCooldownTicks() > 0) {
            double secs = ability.getCooldownTicks() / 20.0;
            String cd = (secs == Math.floor(secs))
                    ? String.valueOf((long) secs) + "s"
                    : secs + "s";
            lines.add(Component.text("    Cooldown:   ", NamedTextColor.GRAY)
                    .decoration(TextDecoration.ITALIC, false)
                    .append(Component.text(cd, NamedTextColor.AQUA)
                            .decoration(TextDecoration.ITALIC, false)));
        }

        return lines;
    }

    // -------------------------------------------------------------------------

    private static Component row(String label, String value) {
        return Component.text("    " + label + ": ", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(value, NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
    }

    private static String formatTriggers(Ability ability) {
        return ability.getTriggers().stream()
                .map(AbilityLoreHelper::triggerName)
                .sorted()
                .reduce((a, b) -> a + " / " + b)
                .orElse("—");
    }

    private static String triggerName(AbilityTrigger trigger) {
        return switch (trigger) {
            case RIGHT_CLICK       -> "Right-Click";
            case LEFT_CLICK        -> "Left-Click";
            case SNEAK_RIGHT_CLICK -> "Sneak + Right-Click";
            case SNEAK_LEFT_CLICK  -> "Sneak + Left-Click";
            case HIT_ENTITY        -> "Attack";
        };
    }
}
