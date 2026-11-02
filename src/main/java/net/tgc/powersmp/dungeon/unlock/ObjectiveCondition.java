package net.tgc.powersmp.dungeon.unlock;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.ConditionType;
import org.bukkit.entity.Player;

public class ObjectiveCondition implements UnlockCondition {
    private final Main plugin;
    private final String objectiveId;
    private final int finalStep;

    public ObjectiveCondition(Main plugin, String objectiveId, int finalStep) {
        this.plugin = plugin;
        this.objectiveId = objectiveId;
        this.finalStep = finalStep;
    }

    @Override
    public ConditionType getType() { return ConditionType.OBJECTIVE_COMPLETE; }

    @Override
    public boolean isMet(Player player) {
        return plugin.getObjectiveManager().isCompleted(player, objectiveId, finalStep);
    }

    @Override
    public String getDescription() {
        return "Complete objective: " + objectiveId;
    }
}