package net.tgc.powersmp.recipe;

public enum RecType {
    MATERIAL("Materials"),
    WEAPON("Weapons & Tools"),
    ARMOR("Armor"),
    DROP("Drops");

    private final String displayName;

    RecType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}