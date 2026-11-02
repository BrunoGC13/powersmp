package net.tgc.powersmp.dungeon.unlock;

import net.tgc.powersmp.dungeon.ConditionType;
import org.bukkit.entity.Player;

public interface UnlockCondition {
    ConditionType getType();
    boolean isMet(Player player);
    String getDescription(); // für GUI/Chat Anzeige
}
