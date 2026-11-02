package net.tgc.powersmp.civilization;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.tgc.powersmp.Main;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CivilizationRegistry {
    private Main plugin;
    public CivilizationRegistry(Main plugin) {
        this.plugin = plugin;
    }

    public List<UUID> getMembers(Civilization civ) {
        List<UUID> memberUUIDs = new ArrayList<>();

        // 1. Safely retrieve the raw text list from the config
        List<String> rawStrings = plugin.getCiviliazationData().getStringList("civilizations." + civ.getName() + ".members");

        // 2. Loop through and convert each String into a UUID
        for (String uuidStr : rawStrings) {
            try {
                memberUUIDs.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                // Skips corrupted or invalid strings in your config file
                plugin.getLogger().warning("Invalid UUID found in config for civ " + civ.getName() + ": " + uuidStr);
            }
        }

        return memberUUIDs;
    }

    public List<UUID> getJoinRequests(Civilization civ) {
        List<UUID> joinUUIDs = new ArrayList<>();

        // 1. Safely retrieve the raw text list from the config
        List<String> rawStrings = plugin.getCiviliazationData().getStringList("civilizations." + civ.getName() + ".join-requests");

        // 2. Loop through and convert each String into a UUID
        for (String uuidStr : rawStrings) {
            try {
                joinUUIDs.add(UUID.fromString(uuidStr));
            } catch (IllegalArgumentException e) {
                // Skips corrupted or invalid strings in your config file
                plugin.getLogger().warning("Invalid UUID found in config for civ " + civ.getName() + ": " + uuidStr);
            }
        }

        return joinUUIDs;
    }

    public void setupExistingCivilizationDefense(Civilization civ) {
        // Get the WorldGuard region for this civilization
        ProtectedRegion region = getWorldGuardRegion(civ.getName(), civ.getLocation().getWorld());

        if (region == null) {
            plugin.getLogger().warning("Could not find WorldGuard region for civilization: " +
                    civ.getName());
            return;
        }

        // Get current members from config
        List<UUID> membersList = getMembers(civ);

        // Get the region's current members domain
        DefaultDomain regionMembers = region.getMembers();

        // Clear existing members (in case of re-setup)
        regionMembers.clear();

        // Add all members from config to the WorldGuard region
        for (UUID uuid : membersList) {
            regionMembers.addPlayer(uuid);
        }

        // Set the updated members list back to the region
        region.setMembers(regionMembers);

        plugin.getLogger().info("Successfully updated WorldGuard region members for civilization: " +
                civ.getName());
    }

    public void registerCivilization(Civilization civ) {
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".name", civ.getName());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".owner", civ.getOwner().getName());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".location.world", civ.getLocation().getWorld().getName());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".location.x", civ.getLocation().getX());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".location.y", civ.getLocation().getY());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".location.z", civ.getLocation().getZ());
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".in-war", false);
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".in-war-with", null);

        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".level", 1);
        plugin.getCiviliazationData().set("civilizations." + civ.getName() + ".xp", 0);

        plugin.getCiviliazationData().set(
                "civilizations." + civ.getName() + ".members",
                civ.getOwner().getUniqueId()
        );

        plugin.saveCivilizationsData();

        Location center = civ.getLocation();

// 2D horizontal bounds (X and Z axes)
        int minX = center.getBlockX() - 50;
        int minZ = center.getBlockZ() - 50;

        int maxX = center.getBlockX() + 50;
        int maxZ = center.getBlockZ() + 50;

// Fixed vertical bounds (Bedrock to Sky limit)
        int minY = -64;
        int maxY = 319;

// Execute the region creation
        makeWorldGuardRegion(civ, center.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
    }

    public void makeWorldGuardRegion(Civilization civ, World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        String id = civ.getName();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(world));

        if (regions == null) {
            plugin.getLogger().warning("WorldGuard is not enabled for world: " + world.getName());
            return;
        }

        if (regions.hasRegion(id)) {
            plugin.getLogger().info("Region '" + id + "' already exists in world '" + world.getName() + "'.");
            return;
        }

        // 1. Define the full vertical column points
        BlockVector3 minPoint = BlockVector3.at(minX, minY, minZ);
        BlockVector3 maxPoint = BlockVector3.at(maxX, maxY, maxZ);

        // 2. Create the column region
        ProtectedRegion region = new ProtectedCuboidRegion(id, minPoint, maxPoint);

        // 3. Get the member UUIDs from your config method
        List<UUID> membersList = getMembers(civ);

        // 4. Create a WorldGuard domain and populate it with the UUIDs
        DefaultDomain regionMembers = region.getMembers();
        for (UUID uuid : membersList) {
            regionMembers.addPlayer(uuid);
        }

        // Set the populated domain to the region's members list
        region.setMembers(regionMembers);

        // 5. Save the region to WorldGuard
        regions.addRegion(region);

        plugin.getLogger().info("Successfully created region '" + id + "' with " + membersList.size() + " registered members.");
    }

    public Civilization getCivilization(String name) {
        String path = "civilizations." + name;

        if (!plugin.getCiviliazationData().contains(path)) {
            return null;
        }

        String ownerName = plugin.getCiviliazationData().getString(path + ".owner");

        String worldName = plugin.getCiviliazationData().getString(path + ".location.world");
        double x = plugin.getCiviliazationData().getDouble(path + ".location.x");
        double y = plugin.getCiviliazationData().getDouble(path + ".location.y");
        double z = plugin.getCiviliazationData().getDouble(path + ".location.z");

        assert worldName != null;
        Location location = new Location(
                plugin.getServer().getWorld(worldName),
                x, y, z
        );

        assert ownerName != null;
        Player owner = plugin.getServer().getPlayer(ownerName);

        return new Civilization(name, owner, location);
    }

    public ProtectedRegion getWorldGuardRegion(String id, World world) {
        // 1. Get the main WorldGuard region container
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        // 2. Get the manager for the specific world
        RegionManager regions = container.get(BukkitAdapter.adapt(world));
        if (regions == null) {
            return null; // WorldGuard is not tracking this world
        }

        // 3. Return the region (this will be null if the region doesn't exist)
        return regions.getRegion(id);
    }
}
