package net.tgc.powersmp.menus;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Central registry for all openable GUI menus.
 *
 * Register a menu:
 *   MenuRegistry.register("profile", (viewer, target) -> new ProfileMenu(target));
 *
 * Open a menu:
 *   MenuRegistry.open("profile", viewer, target); // target == viewer for self
 */
public final class MenuRegistry {

    // viewer = who sees the inventory; target = whose data to show (often same as viewer)
    private static final Map<String, BiFunction<Player, Player, Menu>> registry = new LinkedHashMap<>();

    private MenuRegistry() {}

    public static void register(String id, BiFunction<Player, Player, Menu> factory) {
        registry.put(id.toLowerCase(), factory);
    }

    /**
     * Opens the menu for {@code viewer}, passing {@code target} as context.
     * Returns false if no menu with that id exists.
     */
    public static boolean open(String id, Player viewer, Player target) {
        BiFunction<Player, Player, Menu> factory = registry.get(id.toLowerCase());
        if (factory == null) return false;
        factory.apply(viewer, target).displayTo(viewer);
        return true;
    }

    /** Convenience: viewer is their own target. */
    public static boolean open(String id, Player viewer) {
        return open(id, viewer, viewer);
    }

    public static boolean exists(String id) {
        return registry.containsKey(id.toLowerCase());
    }

    public static List<String> getIds() {
        return new ArrayList<>(registry.keySet());
    }
}
