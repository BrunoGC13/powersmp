package net.tgc.powersmp.other;

import org.bukkit.Location;

public class SpecialLocation {
    private final Location location;
    private final String name;
    private final String worldName;

    public SpecialLocation(Location location, String name) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location and world cannot be null.");
        }

        this.location = location.clone();
        this.name = name;
        this.worldName = location.getWorld().getName();
    }

    public SpecialLocation(
            String name,
            String worldName,
            double x,
            double y,
            double z,
            float yaw,
            float pitch
    ) {
        this.name = name;
        this.worldName = worldName;
        this.location = new Location(
                null,
                x,
                y,
                z,
                yaw,
                pitch
        );
    }

    public Location getLocation() {
        return location.clone();
    }

    public String getName() {
        return name;
    }

    public String getWorldName() {
        return worldName;
    }
}