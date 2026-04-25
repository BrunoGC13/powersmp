package net.tgc.powersmp.api.ability;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class AbilityContext {
    private final Player player;
    private final AbilityTrigger trigger;
    private final Event event;

    public AbilityContext(Player player, AbilityTrigger trigger, Event event) {
        this.player = player;
        this.trigger = trigger;
        this.event = event;
    }

    public Player getPlayer() { return player; }
    public AbilityTrigger getTrigger() { return trigger; }
    public Event getEvent() { return event; }
}
