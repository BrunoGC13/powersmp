package net.tgc.powersmp.api.ability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {
    private final Map<String, Ability> abilities = new HashMap<>();
    private static AbilityRegistry instance;

    private AbilityRegistry() {}

    public static AbilityRegistry getInstance() {
        if (instance == null) instance = new AbilityRegistry();
        return instance;
    }

    public void register(Ability ability) {
        abilities.put(ability.getId().toLowerCase(), ability);
    }

    public Ability get(String id) {
        return abilities.get(id.toLowerCase());
    }

    public Collection<Ability> getAll() {
        return Collections.unmodifiableCollection(abilities.values());
    }

    public boolean isRegistered(String id) {
        return abilities.containsKey(id.toLowerCase());
    }
}
