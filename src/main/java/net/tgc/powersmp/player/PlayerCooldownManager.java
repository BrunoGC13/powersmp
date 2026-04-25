package net.tgc.powersmp.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCooldownManager {
    private static PlayerCooldownManager instance;
    // Map<PlayerUUID, Map<AbilityId, ExpiryTimestampMs>>
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();

    private PlayerCooldownManager() {}

    public static PlayerCooldownManager getInstance() {
        if (instance == null) instance = new PlayerCooldownManager();
        return instance;
    }

    public boolean isOnCooldown(Player player, String abilityId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return false;
        Long expiry = map.get(abilityId);
        if (expiry == null) return false;
        if (System.currentTimeMillis() >= expiry) {
            map.remove(abilityId);
            return false;
        }
        return true;
    }

    /** Returns remaining cooldown in ticks (0 if not on cooldown). */
    public long getRemainingCooldownTicks(Player player, String abilityId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map == null) return 0;
        Long expiry = map.get(abilityId);
        if (expiry == null) return 0;
        long remainingMs = expiry - System.currentTimeMillis();
        return remainingMs > 0 ? remainingMs / 50 : 0;
    }

    public void setCooldown(Player player, String abilityId, long ticks) {
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(abilityId, System.currentTimeMillis() + (ticks * 50L));
    }

    public void clearCooldown(Player player, String abilityId) {
        Map<String, Long> map = cooldowns.get(player.getUniqueId());
        if (map != null) map.remove(abilityId);
    }

    public void clearAll(Player player) {
        cooldowns.remove(player.getUniqueId());
    }
}
