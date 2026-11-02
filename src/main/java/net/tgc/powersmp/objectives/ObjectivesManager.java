package net.tgc.powersmp.objectives;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.tgc.powersmp.Main;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.menus.MenuRegistry;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ObjectivesManager {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Main plugin;

    private final int XP_REWARD_ADVANCE;
    private final int XP_REWARD_COMPLETE;

    // player → (questId → step), questId = "npc_<npcId>" for NPC assignments
    private final Map<UUID, Map<String, Integer>> questSteps = new HashMap<>();

    // player → rewarded quest IDs
    private final Map<UUID, Set<String>> rewarded = new HashMap<>();

    // player → (questId → kill progress within the current kill_mob step)
    private final Map<UUID, Map<String, Integer>> killProgress = new HashMap<>();

    // player → active code-driven Objective objects
    private final Map<UUID, List<Objective>> activeObjectives = new HashMap<>();

    // npcId → ordered list of configured steps (from npc_assignments.yml)
    private final Map<Integer, List<NpcStep>> npcAssignments = new HashMap<>();

    // player → NPC IDs the player has already talked to
    private final Map<UUID, Set<Integer>> talkedToNpcs = new HashMap<>();

    private boolean hasTalkedToNpc(Player player, int npcId) {
        return talkedToNpcs
                .getOrDefault(player.getUniqueId(), Collections.emptySet())
                .contains(npcId);
    }

    private void markTalkedToNpc(Player player, int npcId) {
        talkedToNpcs
                .computeIfAbsent(player.getUniqueId(), key -> new HashSet<>())
                .add(npcId);
    }

    public ObjectivesManager(Main plugin) {
        this.plugin = plugin;

        this.XP_REWARD_ADVANCE = plugin.getConfig().getInt("xp-rewards.advance_objective", 2);
        this.XP_REWARD_COMPLETE = plugin.getConfig().getInt("xp-rewards.complete_objective", 10);
    }

    // ─────────────────────────────────────────────
    // NPC ASSIGNMENT CONFIG
    // ─────────────────────────────────────────────

    public void loadNpcAssignments(FileConfiguration config) {
        npcAssignments.clear();
        ConfigurationSection root = config.getConfigurationSection("assignments");
        if (root == null) {
            plugin.getLogger().info("[Assignments] No 'assignments' section in npc_assignments.yml");
            return;
        }
        for (String npcIdStr : root.getKeys(false)) {
            int npcId;
            try { npcId = Integer.parseInt(npcIdStr); }
            catch (NumberFormatException e) { continue; }

            ConfigurationSection npcSec = root.getConfigurationSection(npcIdStr);
            if (npcSec == null) continue;
            ConfigurationSection stepsSec = npcSec.getConfigurationSection("steps");
            if (stepsSec == null) continue;

            List<NpcStep> steps = new ArrayList<>();
            stepsSec.getKeys(false).stream()
                    .mapToInt(k -> { try { return Integer.parseInt(k); } catch (NumberFormatException e) { return -1; } })
                    .filter(i -> i >= 0)
                    .sorted()
                    .forEach(idx -> {
                        ConfigurationSection sec = stepsSec.getConfigurationSection(String.valueOf(idx));
                        if (sec != null) {
                            try { steps.add(NpcStep.parse(sec)); }
                            catch (Exception e) {
                                plugin.getLogger().warning("[Assignments] Bad step " + idx + " for NPC " + npcId + ": " + e.getMessage());
                            }
                        }
                    });

            if (!steps.isEmpty()) {
                npcAssignments.put(npcId, steps);
                plugin.getLogger().info("[Assignments] Loaded " + steps.size() + " step(s) for NPC " + npcId);
            }
        }
    }

    // ─────────────────────────────────────────────
    // NPC CLICK HANDLING
    // ─────────────────────────────────────────────

    /**
     * Called when a player right-clicks an NPC.
     * @return true if an assignment step was handled; false to fall through to the menu system.
     */
    public boolean handleNpcClick(Player player, int npcId, String npcName) {
        // Complete any talk_to_npc steps from OTHER NPCs that are waiting for this NPC
        completeTalkToNpcSteps(player, npcId);

        List<NpcStep> steps = npcAssignments.get(npcId);
        if (steps == null) return false;

        String questId = "npc_" + npcId;
        int currentIdx = getStep(player, questId);

        if (currentIdx >= steps.size()) return false; // all done, fall through

        NpcStep step = steps.get(currentIdx);

        if (step.requiredTalkedToNpcId >= 0
                && !hasTalkedToNpc(player, step.requiredTalkedToNpcId)) {
            player.sendMessage(MM.deserialize(
                    "<grey>[<gold>Assignment<grey>] <reset><white>"
                            + "You must first talk to NPC <yellow>#"
                            + step.requiredTalkedToNpcId
                            + "<white>."
            ));
            return true;
        }

        if (!hasRequiredQuestStep(player, step)) {
            sendQuestStepRequirement(player, step);
            return true;
        }

        if (plugin.isDebugMode()) {
            plugin.getLogger().info("[Assignments] " + player.getName() + " clicked NPC " + npcId
                    + " step=" + currentIdx + "/" + steps.size() + " type=" + step.type);
        }

        switch (step.type) {
            case DIALOGUE    -> runDialogue(player, questId, steps, currentIdx, step);
            case KILL_MOB    -> runKillMobClick(player, questId, step);
            case REWARD      -> runReward(player, questId, steps, currentIdx, step);
            case TALK_TO_NPC -> runTalkToNpcClick(player, step);
            case GRANT_PERMISSION -> handleGrantPermission(player, questId, steps, currentIdx, step);
            case SKILL_LEVEL -> handleRequiredSkillLevel(player, questId, steps, currentIdx, step);
            case LEVEL -> handleRequiredLevel(player, questId, steps, currentIdx, step);
            case HAND_OVER_ITEM -> handleRequiredHandOverItem(player, questId, steps, currentIdx, step);
            case HAVE_ITEM -> handleHaveItem(player, questId, steps, currentIdx, step);
            case HAVE_MULTIPLE_ITEMS -> handleHaveMultipleItems(player, questId, steps, currentIdx, step);
            case HAND_OVER_MULTIPLE_ITEMS -> handleHandOverMultipleItems(player, questId, steps, currentIdx, step);
        }
        return true;
    }

    private void handleHandOverMultipleItems(
            Player player,
            String questId,
            List<NpcStep> steps,
            int idx,
            NpcStep step
    ) {
        Map<ItemStack, Integer> requiredItems = step.requiredHaveItems;

        if (requiredItems == null || requiredItems.isEmpty()) {
            sendSequence(player, step.inProgress, step.delayTicks, null);
            return;
        }

        ItemStack[] contents = player.getInventory().getContents();

        // First verify every required item without removing anything.
        for (Map.Entry<ItemStack, Integer> requirement : requiredItems.entrySet()) {
            ItemStack requiredItem = requirement.getKey();
            int requiredAmount = requirement.getValue();

            int availableAmount = 0;

            for (ItemStack inventoryItem : contents) {
                if (matchesRequiredItem(requiredItem, inventoryItem)) {
                    availableAmount += inventoryItem.getAmount();
                }
            }

            if (availableAmount < requiredAmount) {
                sendSequence(player, step.inProgress, step.delayTicks, null);
                return;
            }
        }

        // All requirements are available, so now remove them.
        for (Map.Entry<ItemStack, Integer> requirement : requiredItems.entrySet()) {
            ItemStack requiredItem = requirement.getKey();
            int remainingToRemove = requirement.getValue();

            for (int slot = 0;
                 slot < contents.length && remainingToRemove > 0;
                 slot++) {

                ItemStack inventoryItem = player.getInventory().getItem(slot);

                if (!matchesRequiredItem(requiredItem, inventoryItem)) {
                    continue;
                }

                int removeAmount = Math.min(
                        remainingToRemove,
                        inventoryItem.getAmount()
                );

                int newAmount = inventoryItem.getAmount() - removeAmount;

                if (newAmount <= 0) {
                    player.getInventory().setItem(slot, null);
                } else {
                    inventoryItem.setAmount(newAmount);
                    player.getInventory().setItem(slot, inventoryItem);
                }

                remainingToRemove -= removeAmount;
            }
        }

        sendSequence(
                player,
                step.onComplete,
                step.delayTicks,
                () -> advance(player, questId, steps, idx)
        );
    }

    private void runDialogue(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        sendSequence(player, step.messages, step.delayTicks, () -> {
            String npcIdText = questId.substring("npc_".length());

            try {
                markTalkedToNpc(player, Integer.parseInt(npcIdText));
            } catch (NumberFormatException ignored) {
                plugin.getLogger().warning(
                        "[Assignments] Invalid NPC quest ID: " + questId
                );
            }

            savePlayer(player);
            advance(player, questId, steps, idx);
        });
    }

    private boolean hasRequiredQuestStep(Player player, NpcStep step) {
        if (step.requiredQuestId == null
                || step.requiredQuestStep < 0) {
            return true;
        }

        return getStep(
                player,
                step.requiredQuestId
        ) >= step.requiredQuestStep;
    }

    private void sendQuestStepRequirement(
            Player player,
            NpcStep step
    ) {
        player.sendMessage(MM.deserialize(
                "<grey>[<gold>Assignment<grey>] <reset>"
                        + "<white>You must complete more of another assignment first."
        ));
    }

    private void runKillMobClick(Player player, String questId, NpcStep step) {
        int progress = getKillProgress(player.getUniqueId(), questId);
        if (!step.inProgress.isEmpty()) {
            sendSequence(player, step.inProgress, step.delayTicks, null);
        } else {
            player.sendMessage(MM.deserialize(
                    "<grey>[<gold>Assignment<grey>] <reset><white>Kill progress: <yellow>"
                    + progress + "<white>/<yellow>" + step.requiredKills + " <white>" + step.mobName));
        }
    }

    private void runReward(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        for (NpcStep.RewardItem reward : step.rewards) {
            ItemStack item = buildStack(reward);
            if (item != null) {
                player.getInventory().addItem(item).forEach(
                        (slot, leftover) -> player.getWorld().dropItemNaturally(player.getLocation(), leftover));
            }
        }
        sendSequence(player, step.messages, step.delayTicks, () -> advance(player, questId, steps, idx));
    }

    private void handleRequiredSkillLevel(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        String skillName = step.requiredSkill;
        int skillLevel = step.requiredSkillLevel;

        Skill skill = plugin.getSkillsRegistry().getSkillData(skillName);

        int currentLevel = plugin.getSkillsManager().getLevel(player, skill);
        if (currentLevel < skillLevel) {
            sendSequence(
                    player,
                    step.inProgress,
                    step.delayTicks,
                    null
            );
            return;
        }

        sendSequence(
                player,
                step.messages,
                step.delayTicks,
                () -> advance(player, questId, steps, idx)
        );
    }

    private void handleRequiredLevel(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        int level = step.requiredLevel;

        int currentLevel = plugin.getLevelManager().getLevel(player);
        if (currentLevel < level) {
            sendSequence(
                    player,
                    step.inProgress,
                    step.delayTicks,
                    null
            );
            return;
        }

        sendSequence(
                player,
                step.messages,
                step.delayTicks,
                () -> advance(player, questId, steps, idx)
        );
    }

    private void handleRequiredHandOverItem(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        ItemStack requiredItem = step.requiredHandOverItem;
        int requiredAmount = step.handOverItemAmount;

        if (requiredItem == null) return;

        Inventory inv = player.getInventory();
        ItemStack[] items = inv.getContents();

        for (ItemStack item : items) {
            if (item.getType().equals(requiredItem.getType())) {
                if (item.getAmount() >= requiredAmount) {
                    ItemStack clone = item.clone();
                    clone.setAmount(requiredAmount);
                    inv.remove(clone);
                    sendSequence(player, step.messages, step.delayTicks, () -> advance(player, questId, steps, idx));
                    return;
                }
            }
        }

        sendSequence(player, step.inProgress, step.delayTicks, null);
    }

    private void handleHaveItem(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        ItemStack requiredItem = step.requiredHaveItem;
        int requiredAmount = step.haveItemAmount;

        if (requiredItem == null || requiredAmount <= 0) {
            sendSequence(player, step.inProgress, step.delayTicks, null);
            return;
        }

        int inventoryAmount = 0;

        for (ItemStack inventoryItem : player.getInventory().getContents()) {
            if (matchesRequiredItem(requiredItem, inventoryItem)) {
                inventoryAmount += inventoryItem.getAmount();
            }
        }

        if (inventoryAmount >= requiredAmount) {
            sendSequence(player, step.messages, step.delayTicks, () -> advance(player, questId, steps, idx));
            return;
        }

        sendSequence(player, step.inProgress, step.delayTicks, null);
    }

    private void handleHaveMultipleItems(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        Map<ItemStack, Integer> requiredItems = step.requiredHaveItems;

        if (requiredItems == null || requiredItems.isEmpty()) {
            sendSequence(player, step.inProgress, step.delayTicks, null);
            return;
        }

        ItemStack[] inventoryContents = player.getInventory().getContents();

        for (Map.Entry<ItemStack, Integer> requirement : requiredItems.entrySet()) {
            ItemStack requiredItem = requirement.getKey();
            int requiredAmount = requirement.getValue();

            int inventoryAmount = 0;

            for (ItemStack inventoryItem : inventoryContents) {
                if (matchesRequiredItem(requiredItem, inventoryItem)) {
                    inventoryAmount += inventoryItem.getAmount();
                }
            }

            if (inventoryAmount < requiredAmount) {
                sendSequence(player, step.inProgress, step.delayTicks, null);
                return;
            }
        }

        sendSequence(player, step.messages, step.delayTicks, () -> advance(player, questId, steps, idx));
    }

    private boolean matchesRequiredItem(ItemStack requiredItem, ItemStack inventoryItem) {
        if (requiredItem == null || inventoryItem == null || inventoryItem.getAmount() <= 0) {
            return false;
        }

        CustomItemRegistry registry = CustomItemRegistry.getInstance();

        Optional<net.tgc.powersmp.api.item.CustomItem> requiredCustomItem =
                registry.fromItemStack(requiredItem);

        if (requiredCustomItem.isPresent()) {
            Optional<net.tgc.powersmp.api.item.CustomItem> inventoryCustomItem =
                    registry.fromItemStack(inventoryItem);

            return inventoryCustomItem.isPresent()
                    && inventoryCustomItem.get().getKey()
                    .equals(requiredCustomItem.get().getKey());
        }

        return inventoryItem.getType() == requiredItem.getType();
    }

    private void handleGrantPermission(Player player, String questId, List<NpcStep> steps, int idx, NpcStep step) {
        String permission = step.permission;

        User user = plugin.getLuckPermsApi().getPlayerAdapter(Player.class).getUser(player);
        user.data().add(Node.builder(permission).build());
        plugin.getLuckPermsApi().getUserManager().saveUser(user);

        sendSequence(player, step.messages, step.delayTicks, () -> advance(player, questId, steps, idx));
    }

    private void runTalkToNpcClick(Player player, NpcStep step) {
        // Player clicked the originating NPC while the objective is not done yet — remind them
        if (!step.inProgress.isEmpty()) {
            sendSequence(player, step.inProgress, step.delayTicks, null);
        } else if (step.targetNpcId >= 0) {
            player.sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(
                    "<grey>[<gold>Assignment<grey>] <reset><white>Find and talk to NPC <yellow>#" + step.targetNpcId + "<white>."));
        }
    }

    /**
     * Called on every NPC click. Scans all OTHER NPC sequences for a TALK_TO_NPC step
     * targeting this NPC and advances it if found.
     */
    private void completeTalkToNpcSteps(Player player, int clickedNpcId) {
        for (Map.Entry<Integer, List<NpcStep>> entry : npcAssignments.entrySet()) {
            if (entry.getKey() == clickedNpcId) continue; // skip the NPC's own sequence

            String questId = "npc_" + entry.getKey();
            List<NpcStep> steps = entry.getValue();
            int currentIdx = getStep(player, questId);
            if (currentIdx < 0 || currentIdx >= steps.size()) continue;

            NpcStep step = steps.get(currentIdx);
            if (step.type != NpcStepType.TALK_TO_NPC) continue;
            if (step.targetNpcId != clickedNpcId) continue;

            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[Assignments] " + player.getName()
                        + " completed talk_to_npc step for " + questId + " by clicking NPC " + clickedNpcId);
            }

            if (!step.onComplete.isEmpty()) {
                sendSequence(player, step.onComplete, step.delayTicks, null);
            }
            markTalkedToNpc(player, clickedNpcId);
            savePlayer(player);
            advance(player, questId, steps, currentIdx);
        }
    }

    private ItemStack buildStack(NpcStep.RewardItem reward) {
        if (reward.customItemKey() != null) {
            try {
                String[] parts = reward.customItemKey().split(":", 2);
                NamespacedKey key = new NamespacedKey(parts[0], parts[1]);
                ItemStack stack = CustomItemRegistry.getInstance().createItemStack(key);
                if (stack != null) {
                    stack.setAmount(reward.amount());
                    return stack;
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[Assignments] Unknown custom_item: " + reward.customItemKey());
            }
        }
        if (reward.material() != null) return new ItemStack(reward.material(), reward.amount());
        return null;
    }

    private void advance(Player player, String questId, List<NpcStep> steps, int currentIdx) {
        int nextIdx = currentIdx + 1;
        questSteps.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(questId, nextIdx);
        savePlayer(player);

        // ── NEU: Area-Unlock prüfen ──────────────────────────
        String dungeonId = plugin.getDungeonAreaManager().getDungeonIdForPlayer(player);
        if (dungeonId != null) {
            plugin.getDungeonAreaManager().checkUnlock(player, dungeonId);
        }
        // ────────────────────────────────────────────────────

        if (nextIdx >= steps.size()) {
            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[Assignments] " + player.getName() + " finished all steps for " + questId);
            }
            return;
        }

        NpcStep next = steps.get(nextIdx);
        if (!hasRequiredQuestStep(player, next)) {
            return;
        }

        if (next.type == NpcStepType.DIALOGUE) {
            sendSequence(
                    player,
                    next.messages,
                    next.delayTicks,
                    () -> advance(player, questId, steps, nextIdx)
            );
        }

        plugin.getLevelManager().addXp(player, 2);
    }

    // ─────────────────────────────────────────────
    // MOB KILL TRACKING
    // ─────────────────────────────────────────────

    /** Called by NpcAssignmentListener when a player kills an entity. */
    public void onMobKill(Player killer, String mobName) {
        UUID uuid = killer.getUniqueId();

        for (Map.Entry<Integer, List<NpcStep>> entry : npcAssignments.entrySet()) {
            String questId = "npc_" + entry.getKey();
            List<NpcStep> steps = entry.getValue();
            int currentIdx = getStep(killer, questId);
            if (currentIdx < 0 || currentIdx >= steps.size()) continue;

            NpcStep step = steps.get(currentIdx);
            if (step.type != NpcStepType.KILL_MOB) continue;
            if (!step.mobName.equalsIgnoreCase(mobName)) continue;

            int progress = getKillProgress(uuid, questId) + 1;

            if (plugin.isDebugMode()) {
                plugin.getLogger().info("[Assignments] " + killer.getName() + " killed " + mobName
                        + " progress=" + progress + "/" + step.requiredKills + " for " + questId);
            }

            if (progress >= step.requiredKills) {
                setKillProgress(uuid, questId, 0);
                if (!step.onComplete.isEmpty()) {
                    sendSequence(killer, step.onComplete, step.delayTicks, null);
                }
                advance(killer, questId, steps, currentIdx);
            } else {
                setKillProgress(uuid, questId, progress);
                killer.sendMessage(MM.deserialize(
                        "<grey>[<gold>Assignment<grey>] <reset><white>Kill progress: <yellow>"
                        + progress + "<white>/<yellow>" + step.requiredKills + " <white>" + step.mobName));
            }
        }
    }

    private int getKillProgress(UUID uuid, String questId) {
        return killProgress.getOrDefault(uuid, Collections.emptyMap()).getOrDefault(questId, 0);
    }

    private void setKillProgress(UUID uuid, String questId, int count) {
        killProgress.computeIfAbsent(uuid, k -> new HashMap<>()).put(questId, count);
    }

    // ─────────────────────────────────────────────
    // CODE-DRIVEN OBJECTIVE API (Objective builder)
    // ─────────────────────────────────────────────

    public void give(Player player, Objective objective) {
        activeObjectives.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>()).add(objective);
        String name = objective.getDisplayName() != null ? objective.getDisplayName() : objective.getId();
        player.sendMessage(MM.deserialize(
                "<grey>[<gold>Assignment<grey>] <reset><white>New objective: <yellow>" + name));
    }

    /** Returns true if the player has an active (code-driven) Objective with this ID. */
    public boolean has(Player player, String objectiveId) {
        if (objectiveId == null) return false;
        List<Objective> list = activeObjectives.get(player.getUniqueId());
        if (list == null) return false;
        return list.stream().anyMatch(o -> o.getId().equals(objectiveId));
    }

    /** Completes the active Objective with the given ID for this player. */
    public void complete(Player player, String objectiveId) {
        List<Objective> list = activeObjectives.get(player.getUniqueId());
        if (list == null) return;
        plugin.getLevelManager().addXp(player, 10);
        list.stream()
            .filter(o -> o.getId().equals(objectiveId) && !o.isCompleted())
            .findFirst()
            .ifPresent(o -> o.complete(player));
    }

    /** Returns the highest-priority incomplete active Objective for this player. */
    public Optional<Objective> getTopObjective(Player player) {
        List<Objective> candidates = new ArrayList<>();

        // Code-driven objectives
        List<Objective> codeObjs = activeObjectives.get(player.getUniqueId());
        if (codeObjs != null) {
            codeObjs.stream().filter(o -> !o.isCompleted()).forEach(candidates::add);
        }

        // YAML assignment steps — synthesize display objectives for KILL_MOB and REWARD steps
        for (Map.Entry<Integer, List<NpcStep>> entry : npcAssignments.entrySet()) {
            String questId  = "npc_" + entry.getKey();
            List<NpcStep> steps = entry.getValue();
            int idx = getStep(player, questId);
            if (idx < 0 || idx >= steps.size()) continue;

            NpcStep step = steps.get(idx);
            if (step.type == NpcStepType.DIALOGUE) continue; // dialogue is transient, don't display
            if (step.displayName == null) continue;

            candidates.add(Objective.of(questId + "_step_" + idx)
                    .name(step.displayName)
                    .icon(step.icon)
                    .priority(step.priority));
        }

        return candidates.stream().max(Comparator.comparingInt(Objective::getPriority));
    }

    public List<Objective> getActiveObjectives(Player player) {
        return activeObjectives.getOrDefault(player.getUniqueId(), Collections.emptyList());
    }

    public void completeObjective(Player player, String objectiveId) {
        complete(player, objectiveId);
        plugin.getLevelManager().addXp(player, 5);
    }

    // ─────────────────────────────────────────────
    // STEP SYSTEM (generic — also used by quests)
    // ─────────────────────────────────────────────

    public int getStep(Player player, String questId) {
        return questSteps.getOrDefault(player.getUniqueId(), Collections.emptyMap()).getOrDefault(questId, 0);
    }

    public void setStep(Player player, String questId, int step) {
        questSteps.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(questId, step);
        savePlayer(player);
    }

    public void nextStep(Player player, String questId) {
        setStep(player, questId, getStep(player, questId) + 1);
    }

    // ─────────────────────────────────────────────
    // REWARD TRACKING (existing API)
    // ─────────────────────────────────────────────

    public boolean hasReward(Player player, String questId) {
        return rewarded.getOrDefault(player.getUniqueId(), Collections.emptySet()).contains(questId);
    }

    public void giveReward(Player player, String questId) {
        if (hasReward(player, questId)) return;
        rewarded.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>()).add(questId);
        player.sendMessage("§aQuest completed! Reward given.");
        savePlayer(player);
    }

    public boolean isCompleted(Player player, String questId, int finalStep) {
        return getStep(player, questId) >= finalStep;
    }

    // ─────────────────────────────────────────────
    // PERSISTENCE
    // ─────────────────────────────────────────────

    public void savePlayer(Player player) {
        savePlayerByUUID(player.getUniqueId());
    }

    private void savePlayerByUUID(UUID uuid) {
        var config = plugin.getPlayerData();
        String base = "players." + uuid + ".objectives";
        config.set(base, null);

        Map<String, Integer> steps = questSteps.getOrDefault(uuid, Collections.emptyMap());
        Set<String> rewardedSet    = rewarded.getOrDefault(uuid, Collections.emptySet());
        Map<String, Integer> kills = killProgress.getOrDefault(uuid, Collections.emptyMap());
        Set<Integer> talkedNpcIds = talkedToNpcs.getOrDefault(uuid, Collections.emptySet());

        for (String questId : steps.keySet()) {
            config.set(base + "." + questId + ".step", steps.get(questId));
            config.set(base + "." + questId + ".rewarded", rewardedSet.contains(questId));
            config.set(base + "." + questId + ".kill_progress", kills.getOrDefault(questId, 0));
        }

        config.set(base + ".talked_to_npcs", new ArrayList<>(talkedNpcIds));

        plugin.savePlayerData();
    }

    public void loadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        var config = plugin.getPlayerData();
        String base = "players." + uuid + ".objectives";
        if (!config.contains(base)) return;

        ConfigurationSection section = config.getConfigurationSection(base);
        if (section == null) return;

        Map<String, Integer> steps = new HashMap<>();
        Set<String> rewardedSet = new HashSet<>();
        Map<String, Integer> kills = new HashMap<>();
        Set<Integer> talkedNpcIds = new HashSet<>();

        List<?> savedTalkedNpcIds =
                config.getList(base + ".talked_to_npcs", Collections.emptyList());

        for (Object value : savedTalkedNpcIds) {
            if (value instanceof Number number) {
                talkedNpcIds.add(number.intValue());
            }
        }

        for (String questId : section.getKeys(false)) {
            if (questId.equals("talked_to_npcs")) continue;

            steps.put(questId, config.getInt(base + "." + questId + ".step", 0));
            if (config.getBoolean(base + "." + questId + ".rewarded", false)) {
                rewardedSet.add(questId);
            }
            kills.put(questId, config.getInt(base + "." + questId + ".kill_progress", 0));
        }

        questSteps.put(uuid, steps);
        rewarded.put(uuid, rewardedSet);
        killProgress.put(uuid, kills);
        talkedToNpcs.put(uuid, talkedNpcIds);
    }

    public void clearPlayer(UUID uuid) {
        questSteps.remove(uuid);
        rewarded.remove(uuid);
        killProgress.remove(uuid);
        talkedToNpcs.remove(uuid);
        activeObjectives.remove(uuid);
    }

    public void clear(Player player) {
        clearPlayer(player.getUniqueId());
    }

    // ─────────────────────────────────────────────
    // ADMIN HELPERS (used by reset/set commands)
    // ─────────────────────────────────────────────

    /** Clears all in-memory and saved objective progress for a player. */
    public void resetObjectives(UUID uuid) {
        clearPlayer(uuid);
        plugin.getPlayerData().set("players." + uuid + ".objectives", null);
        plugin.savePlayerData();
    }

    /** Sets a specific objective step directly, for both online state and YAML. */
    public void setObjectiveStep(UUID uuid, String questId, int step) {
        questSteps.computeIfAbsent(uuid, k -> new HashMap<>()).put(questId, step);
        killProgress.computeIfAbsent(uuid, k -> new HashMap<>()).put(questId, 0);
        savePlayerByUUID(uuid);
    }

    // ─────────────────────────────────────────────
    // UTILITY
    // ─────────────────────────────────────────────

    private void sendSequence(Player player, List<String> messages, long delayTicks, Runnable onDone) {
        runMsg(player, messages, delayTicks, 0, onDone);
    }

    private void runMsg(Player player, List<String> messages, long delayTicks, int index, Runnable onDone) {
        if (index >= messages.size()) {
            if (onDone != null) onDone.run();
            return;
        }
        long wait = (index == 0) ? 0L : delayTicks;
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            player.sendMessage(MM.deserialize(messages.get(index)));
            runMsg(player, messages, delayTicks, index + 1, onDone);
        }, wait);
    }
}
