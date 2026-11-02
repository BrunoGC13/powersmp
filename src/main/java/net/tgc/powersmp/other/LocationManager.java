package net.tgc.powersmp.other;

import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LocationManager {
    private final Main plugin;
    private final Map<String, SpecialLocation> locations = new LinkedHashMap<>();

    public LocationManager(Main plugin) {
        this.plugin = plugin;
        loadLocations();
    }

    public void loadLocations() {
        locations.clear();

        ConfigurationSection section =
                plugin.getSpecialLocationsData().getConfigurationSection("locations");

        if (section == null) {
            return;
        }

        for (String rawName : section.getKeys(false)) {
            ConfigurationSection locationSection =
                    section.getConfigurationSection(rawName);

            if (locationSection == null) {
                continue;
            }

            String worldName = locationSection.getString("world");

            if (worldName == null || worldName.isBlank()) {
                plugin.getLogger().warning(
                        "Special location '" + rawName + "' has no world."
                );
                continue;
            }

            SpecialLocation specialLocation = new SpecialLocation(
                    rawName,
                    worldName,
                    locationSection.getDouble("x"),
                    locationSection.getDouble("y"),
                    locationSection.getDouble("z"),
                    (float) locationSection.getDouble("yaw"),
                    (float) locationSection.getDouble("pitch")
            );

            locations.put(normalize(rawName), specialLocation);
        }
    }

    public boolean addLocation(Location location, String name) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        String normalizedName = normalize(name);

        if (!isValidName(normalizedName)) {
            return false;
        }

        SpecialLocation specialLocation =
                new SpecialLocation(location, normalizedName);

        locations.put(normalizedName, specialLocation);
        saveLocations();

        return true;
    }

    public boolean removeLocation(String name) {
        String normalizedName = normalize(name);

        if (locations.remove(normalizedName) == null) {
            return false;
        }

        saveLocations();
        return true;
    }

    public SpecialLocation getLocation(String name) {
        return locations.get(normalize(name));
    }

    public List<String> getLocationNames() {
        return Collections.unmodifiableList(
                new ArrayList<>(locations.keySet())
        );
    }

    public void saveLocations() {
        plugin.getSpecialLocationsData().set("locations", null);

        for (SpecialLocation specialLocation : locations.values()) {
            String path = "locations." + specialLocation.getName();
            Location location = specialLocation.getLocation();

            plugin.getSpecialLocationsData().set(
                    path + ".world",
                    specialLocation.getWorldName()
            );
            plugin.getSpecialLocationsData().set(
                    path + ".x",
                    location.getX()
            );
            plugin.getSpecialLocationsData().set(
                    path + ".y",
                    location.getY()
            );
            plugin.getSpecialLocationsData().set(
                    path + ".z",
                    location.getZ()
            );
            plugin.getSpecialLocationsData().set(
                    path + ".yaw",
                    location.getYaw()
            );
            plugin.getSpecialLocationsData().set(
                    path + ".pitch",
                    location.getPitch()
            );
        }

        plugin.saveSpecialLocationsData();
    }

    public Location resolveLocation(SpecialLocation specialLocation) {
        if (specialLocation == null) {
            return null;
        }

        World world = Bukkit.getWorld(specialLocation.getWorldName());

        if (world == null) {
            return null;
        }

        Location location = specialLocation.getLocation();
        location.setWorld(world);

        return location;
    }

    private String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    private boolean isValidName(String name) {
        return name.matches("[a-z0-9_-]+");
    }
}