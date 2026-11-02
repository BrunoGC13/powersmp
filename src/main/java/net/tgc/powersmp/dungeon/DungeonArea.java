package net.tgc.powersmp.dungeon;

import net.tgc.powersmp.dungeon.unlock.UnlockCondition;
import org.bukkit.Location;

import java.util.List;

public class DungeonArea {
    private String name;
    private int id;
    private Dungeon dungeon;
    private Location border1;
    private Location border2;
    private Location pos1;
    private Location pos2;

    public DungeonArea(String name, int id, Location border1, Location border2, Location pos1, Location pos2, Dungeon dungeon) {
        this.name = name;
        this.id = id;
        this.border1 = border1;
        this.border2 = border2;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.dungeon = dungeon;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Location getBorder1() {
        return border1;
    }

    public Location getBorder2() {
        return border2;
    }

    public Location getPos1() {
        return pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    // In DungeonArea — neue Felder + Konstruktor anpassen:
    private List<UnlockCondition> unlockConditions;

    public List<UnlockCondition> getUnlockConditions() {
        return unlockConditions;
    }

    public void setUnlockConditions(List<UnlockCondition> conditions) {
        this.unlockConditions = conditions;
    }
}
