package net.tgc.powersmp.objectives;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Objective {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final String id;
    private String displayName;
    private String description;
    private int priority = 0;
    private boolean completed = false;
    private Consumer<Player> onComplete;
    private final List<String> scoreboardLines = new ArrayList<>();
    private String completionMessage = "<green>✔ Objective complete: ";
    private String icon = "◈";

    // Private constructor — use builder
    private Objective(String id) {
        this.id = id;
    }

    // ── Builder entry point ──────────────────────────────────────────────────

    public static Objective of(String id) {
        return new Objective(id);
    }

    // ── Builder methods ──────────────────────────────────────────────────────

    public Objective name(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public Objective description(String description) {
        this.description = description;
        return this;
    }

    public Objective priority(int priority) {
        this.priority = priority;
        return this;
    }

    public Objective icon(String icon) {
        this.icon = icon;
        return this;
    }

    public Objective completionMessage(String message) {
        this.completionMessage = message;
        return this;
    }

    public Objective onComplete(Consumer<Player> action) {
        this.onComplete = action;
        return this;
    }

    public Objective scoreboardLines(String... lines) {
        this.scoreboardLines.clear();
        this.scoreboardLines.addAll(List.of(lines));
        return this;
    }

    // ── Runtime methods ──────────────────────────────────────────────────────

    public void complete(Player player) {
        if (completed) return;
        completed = true;
        player.sendMessage(MM.deserialize(completionMessage + displayName));
        if (onComplete != null) onComplete.accept(player);
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()                  { return id; }
    public String getDisplayName()         { return displayName; }
    public String getDescription()         { return description; }
    public int getPriority()               { return priority; }
    public boolean isCompleted()           { return completed; }
    public List<String> getScoreboardLines() { return scoreboardLines; }
    public String getIcon()                { return icon; }
}