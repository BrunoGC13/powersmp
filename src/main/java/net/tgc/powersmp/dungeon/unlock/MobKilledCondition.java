package net.tgc.powersmp.dungeon.unlock;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.ConditionType;
import org.bukkit.entity.Player;

public class MobKilledCondition implements UnlockCondition {
    private final Main plugin;
    private final String mobId;
    private final int required;

    public MobKilledCondition(Main plugin, String mobId, int required) {
        this.plugin = plugin;
        this.mobId = mobId;
        this.required = required;
    }

    @Override
    public ConditionType getType() { return ConditionType.MOB_KILLED; }

    @Override
    public boolean isMet(Player player) {
        int kills = plugin.getDungeonPlayerData().getKills(player, mobId);
        return kills >= required;
    }

    @Override
    public String getDescription() {
        return "Kill " + required + "x " + mobId;
    }
}