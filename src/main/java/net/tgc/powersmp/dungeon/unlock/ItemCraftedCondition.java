package net.tgc.powersmp.dungeon.unlock;

import net.tgc.powersmp.Main;
import net.tgc.powersmp.dungeon.ConditionType;
import org.bukkit.entity.Player;

public class ItemCraftedCondition implements UnlockCondition {
    private final Main plugin;
    private final String itemId;
    private final int required;

    public ItemCraftedCondition(Main plugin, String itemId, int required) {
        this.plugin = plugin;
        this.itemId = itemId;
        this.required = required;
    }

    @Override
    public ConditionType getType() { return ConditionType.ITEM_CRAFTED; }

    @Override
    public boolean isMet(Player player) {
        int crafted = plugin.getDungeonPlayerData().getCrafted(player, itemId);
        return crafted >= required;
    }

    @Override
    public String getDescription() {
        return "Craft " + required + "x " + itemId;
    }
}