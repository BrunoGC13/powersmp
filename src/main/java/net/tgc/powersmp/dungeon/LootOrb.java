package net.tgc.powersmp.dungeon;

import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LootOrb {
    private final UUID entityId;
    private final Location location;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public LootOrb(UUID entityId, Location location) {
        this.entityId = entityId;
        this.location = location;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return location;
    }

    public boolean canLoot(UUID playerUuid, long cooldownMs) {
        Long last = cooldowns.get(playerUuid);
        return last == null || System.currentTimeMillis() - last >= cooldownMs;
    }

    public void markLooted(UUID playerUuid) {
        cooldowns.put(playerUuid, System.currentTimeMillis());
    }
}
