package net.tgc.powersmp.other;

public class Helper {
    public String getLevelIcon(int level, String color) {
        int base;

        switch (color.toLowerCase()) {
            case "blue" -> base = 0xE100;
            case "green" -> base = 0xE110;
            case "red" -> base = 0xE120;
            default -> base = 0xE100;
        }

        int unicode = base + (level - 1);
        return String.valueOf((char) unicode);
    }
}
