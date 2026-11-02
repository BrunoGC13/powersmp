package net.tgc.powersmp.other;

public enum FontIcon {
    BLOCK("\uE000"),
    ACCESSORY("\uE001"),
    ARMOR("\uE002"),
    ARTIFACT("\uE003"),
    COMMON("\uE004"),
    UNCOMMON("\uE005"),
    RARE("\uE006"),
    EPIC("\uE007"),
    LEGENDARY("\uE008");

    private final String unicode;

    FontIcon(String unicode) {
        this.unicode = unicode;
    }

    public String get() {
        return unicode;
    }
}