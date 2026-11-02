package net.tgc.powersmp.civilization;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.tgc.powersmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CivilizationManager {
    private Main plugin;
    public CivilizationManager(Main plugin) {
        this.plugin = plugin;
    }

    public List<String> getAllCivilizationNames() {
        if (!plugin.getCiviliazationData().isConfigurationSection("civilizations")) {
            return List.of();
        }

        var section = plugin.getCiviliazationData().getConfigurationSection("civilizations");
        if (section == null) {
            return List.of();
        }

        return new ArrayList<>(section.getKeys(false));
    }

    public Civilization getCivilization(String name) {
        // Check if the civilization exists in the configuration
        if (!plugin.getCiviliazationData().isSet("civilizations." + name)) {
            return null;
        }

        // Get the owner UUID from config
        String ownerUUIDStr = plugin.getCiviliazationData().getString("civilizations." + name +
                ".owner");
        if (ownerUUIDStr == null) {
            return null;
        }

        UUID ownerUUID = UUID.fromString(ownerUUIDStr);
        Player owner = plugin.getServer().getPlayer(ownerUUID);

        // Get members list
        List<String> membersStr = plugin.getCiviliazationData().getStringList("civilizations." +
                name + ".members");
        List<UUID> members = membersStr.stream()
                .map(UUID::fromString)
                .toList();

        // Get join requests list
        List<String> joinRequestsStr = plugin.getCiviliazationData().getStringList("civilizations." + name + ".join-requests");
        List<UUID> joinRequests = joinRequestsStr.stream()
                .map(UUID::fromString)
                .toList();

        // Get location data
        String worldName = plugin.getCiviliazationData().getString("civilizations." + name + ".location.world");
        double x = plugin.getCiviliazationData().getDouble("civilizations." + name + ".location.x");
        double y = plugin.getCiviliazationData().getDouble("civilizations." + name + ".location.y");
        double z = plugin.getCiviliazationData().getDouble("civilizations." + name + ".location.z");

        // Create and return the Civilization object
        assert worldName != null;
        return new Civilization(name, owner, new Location(Bukkit.getWorld(worldName), x, y, z));
    }

    public String getPlayerCivilization(UUID playerId) {
        // Check if player has a civilization set in the configuration
        if (plugin.getPlayerData().isSet("players." + playerId + ".civilization")) {
            return plugin.getPlayerData().getString("players." + playerId + ".civilization");
        }
        return null;
    }

    public void addMember(Civilization civ, Player p) {
        List<UUID> members = plugin.getCivilizationRegistry().getMembers(civ);
        members.add(p.getUniqueId());

        plugin.getCiviliazationData().set(
                "civilizations." + civ.getName() + ".members",
                members.stream()
                        .map(UUID::toString)
                        .toList()
        );

        plugin.getPlayerData().set("players." + p.getUniqueId() + ".civilization",
                civ.getName());

        ProtectedRegion region =
                plugin.getCivilizationRegistry().getWorldGuardRegion(civ.getName(),
                        civ.getLocation().getWorld());

        if (region == null) {
            plugin.getLogger().warning("Could not find a region named " + civ.getName());
            return;
        }

        DefaultDomain regionMembers = region.getMembers();
        regionMembers.addPlayer(p.getUniqueId());

        plugin.saveCivilizationsData();
    }

    public void requestJoin(Civilization civ, Player p) {
        Player owner = civ.getOwner();

        if (plugin.getPlayerData().getBoolean("players." + p.getUniqueId() +
                ".has-civ-join-request")) return;
        if (!(plugin.getPlayerData().isSet("players." + p.getUniqueId() + ".civilization")))
            return;

        List<UUID> joinRequests = plugin.getCivilizationRegistry().getJoinRequests(civ);
        joinRequests.add(p.getUniqueId());

        plugin.getCiviliazationData().set(
                "civilizations." + civ.getName() + ".join-requests",
                joinRequests.stream()
                        .map(UUID::toString)
                        .toList()
        );
        plugin.getPlayerData().set("players." + p.getUniqueId() + ".has-civ-join-request", true);


        owner.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<yellow>Civilization<gray>] <white>The player <green>" + p.getName() + " <white>wants to join your civilization!"));
        p.sendMessage(MiniMessage.miniMessage().deserialize("<gray>[<yellow>Civilization<gray>] <white>Sent a join request to the <green>" + civ.getName() + " <white>civilization!"));

    }

    /**
     * Remove a player from the request join list for a civilization
     * @param civ The civilization
     * @param p The player to remove from join requests
     */
    public void removePlayerFromJoinRequest(Civilization civ, Player p) {
        // Get the current join requests for this civilization
        List<UUID> joinRequests = plugin.getCivilizationRegistry().getJoinRequests(civ);

        // Remove the player's UUID from the join requests list
        joinRequests.remove(p.getUniqueId());

        // Update the configuration with the modified list
        plugin.getCiviliazationData().set(
                "civilizations." + civ.getName() + ".join-requests",
                joinRequests.stream()
                        .map(UUID::toString)
                        .toList()
        );

        // Note: The has-civ-join-request flag is already handled elsewhere
        // If you need to explicitly set it to false, uncomment the line below:
        // plugin.getPlayerData().set("players." + p.getUniqueId() + ".has-civ-join-request", false);

        // Save the changes
        plugin.saveCivilizationsData();
    }

    public void acceptJoinRequest(Civilization civ, @NonNull Player p) {
        if (!(plugin.getPlayerData().getBoolean("players." + p.getUniqueId() +
                ".has-civ-join-request"))) return;

        plugin.getPlayerData().set("players." + p.getUniqueId() + ".has-civ-join-request",
                false);
        removePlayerFromJoinRequest(civ, p);
        addMember(civ, p);
    }
}