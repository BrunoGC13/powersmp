package net.tgc.powersmp.api.ability;

import java.util.Set;

/**
 * Implement this interface to create a custom ability.
 * Register it via PowerSMPAPI.registerAbility(ability) or AbilityRegistry.getInstance().register(ability).
 */
public interface Ability {
    /** Unique lowercase ID, e.g. "fireball_launch" */
    String getId();

    /** Display name shown to players */
    String getDisplayName();

    /** Which triggers activate this ability */
    Set<AbilityTrigger> getTriggers();

    /** Power consumed per use. Return 0 for no cost. */
    double getPowerCost();

    /** Cooldown in ticks after use. Return 0 for no cooldown. */
    long getCooldownTicks();

    /** Called when the ability is triggered. Power has already been deducted. */
    void execute(AbilityContext context);
}
