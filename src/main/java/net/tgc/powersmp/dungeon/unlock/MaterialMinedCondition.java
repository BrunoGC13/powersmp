package net.tgc.powersmp.dungeon.unlock;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.ConditionType;
import org.bukkit.entity.Player;

public class MaterialMinedCondition implements UnlockCondition {
    private final Main plugin;
    private final String materialId;
    private final int required;

    public MaterialMinedCondition(Main plugin, String materialId, int required) {
        this.plugin = plugin;
        this.materialId = materialId;
        this.required = required;
    }

    @Override
    public ConditionType getType() { return ConditionType.MATERIAL_MINED; }

    @Override
    public boolean isMet(Player player) {
        int mined = plugin.getDungeonPlayerData().getMined(player, materialId);
        return mined >= required;
    }

    @Override
    public String getDescription() {
        return "Mine " + required + "x " + materialId;
    }
}