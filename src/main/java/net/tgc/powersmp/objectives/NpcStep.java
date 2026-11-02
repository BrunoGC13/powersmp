package net.tgc.powersmp.objectives;

import io.lumine.mythic.api.skills.SkillManager;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.skills.Skill;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class NpcStep {

    public final NpcStepType type;
    public final List<String> messages;
    public final List<String> inProgress;
    public final List<String> onComplete;
    public final long delayTicks;
    public final String mobName;
    public final int requiredKills;
    public final List<RewardItem> rewards;
    public final int targetNpcId;   // TALK_TO_NPC: NPC the player must right-click
    public final String permission;

    public final String requiredSkill;
    public final int requiredSkillLevel;

    public final int requiredLevel;

    public final ItemStack requiredHandOverItem;
    public final ItemStack requiredHaveItem;

    public final Map<ItemStack, Integer> requiredHaveItems;

    public final int handOverItemAmount;
    public final int haveItemAmount;

    // Optional — controls how this step appears in the %powersmp_objective% placeholder
    public final String displayName;
    public final String icon;
    public final int priority;
    public final int requiredTalkedToNpcId;
    public final String requiredQuestId;
    public final int requiredQuestStep;

    private NpcStep(NpcStepType type, List<String> messages, List<String> inProgress,
                    List<String> onComplete, long delayTicks, String mobName,
                    int requiredKills, List<RewardItem> rewards, int targetNpcId,
                    String displayName, String icon, int priority, String permission,
                    String skill, int requiredSkillLevel, int requiredLevel, ItemStack requiredHandOverItem,
                    ItemStack requiredHaveItem, int handOverItemAmount, int haveItemAmount,
                    Map<ItemStack, Integer> requiredHaveItems, int requiredTalkedToNpcId,
                    String requiredQuestId, int requiredQuestStep) {
        this.type = type;
        this.messages = messages;
        this.inProgress = inProgress;
        this.onComplete = onComplete;
        this.delayTicks = delayTicks;
        this.mobName = mobName;
        this.requiredKills = requiredKills;
        this.rewards = rewards;
        this.targetNpcId = targetNpcId;
        this.displayName = displayName;
        this.icon = icon;
        this.priority = priority;
        this.permission = permission;

        this.requiredSkill = skill;
        this.requiredSkillLevel = requiredSkillLevel;
        this.requiredLevel = requiredLevel;
        this.requiredHandOverItem = requiredHandOverItem;
        this.requiredHaveItem = requiredHaveItem;
        this.requiredHaveItems = requiredHaveItems;
        this.handOverItemAmount = handOverItemAmount;
        this.haveItemAmount = haveItemAmount;
        this.requiredTalkedToNpcId = requiredTalkedToNpcId;
        this.requiredQuestId = requiredQuestId;
        this.requiredQuestStep = requiredQuestStep;
    }

    public static NpcStep parse(ConfigurationSection s) {
        NpcStepType type;
        try {
            type = NpcStepType.valueOf(s.getString("type", "dialogue").toUpperCase());
        } catch (IllegalArgumentException e) {
            type = NpcStepType.DIALOGUE;
        }

        List<String> messages   = s.getStringList("messages");
        List<String> inProgress = s.getStringList("in_progress");
        List<String> onComplete = s.getStringList("on_complete");
        long delayTicks = Math.round(s.getDouble("delay", 2.0) * 20);
        String mobName      = s.getString("mob", "");
        int requiredKills   = s.getInt("amount", 1);

        List<RewardItem> rewards = new ArrayList<>();
        for (Map<?, ?> raw : s.getMapList("items")) {
            String matStr    = (String) raw.get("material");
            String customKey = (String) raw.get("custom_item");
            int amount = raw.containsKey("amount") ? ((Number) raw.get("amount")).intValue() : 1;
            Material mat = matStr != null ? Material.matchMaterial(matStr) : null;
            rewards.add(new RewardItem(mat, customKey, amount));
        }

        int targetNpcId = s.getInt("target_npc", -1);
        int requiredTalkedToNpcId = s.getInt("requires_talked_to_npc", -1);
        String permission = s.getString("grant_permission", "");

        String skill = s.getString("skill", "");
        int requiredSkillLevel = s.getInt("required_skill_level", 0);

        int requiredLevel = s.getInt("required_level", 0);

        Material handOverMat = Material.getMaterial(s.getString("required_hand_over_material", ""));
        Material haveMat = Material.getMaterial(s.getString("required_material", ""));

        int handOverMatAmount = s.getInt("required_hand_over_material_amount", 0);
        int haveMatAmount = s.getInt("required_material_amount", 0);

        ItemStack handOverItem = null;
        ItemStack haveMatItem = null;

        if (handOverMat != null) {
            handOverItem = new ItemStack(handOverMat, handOverMatAmount);
        }

        if (haveMat != null) {
            haveMatItem = new ItemStack(haveMat, haveMatAmount);
        }

        Map<ItemStack, Integer> requiredHaveItems = new LinkedHashMap<>();

        for (Map<?, ?> raw : s.getMapList("required_items")) {
            String materialName = raw.get("material") instanceof String value
                    ? value
                    : null;

            String customItemName = raw.get("custom_item") instanceof String value
                    ? value
                    : null;

            int amount = raw.get("amount") instanceof Number number
                    ? number.intValue()
                    : 1;

            if (amount <= 0) {
                continue;
            }

            ItemStack requiredItem = null;

            if (materialName != null) {
                Material material = Material.matchMaterial(materialName);

                if (material != null) {
                    requiredItem = new ItemStack(material, 1);
                }
            } else if (customItemName != null) {
                try {
                    NamespacedKey key = NamespacedKey.fromString(customItemName);

                    if (key != null) {
                        requiredItem =
                                CustomItemRegistry.getInstance().createItemStack(key);

                        if (requiredItem != null) {
                            // The amount is stored in the map value.
                            // Keeping the key amount at 1 makes comparison easier.
                            requiredItem.setAmount(1);
                        }
                    }
                } catch (Exception ignored) {
                    // Invalid custom item keys are ignored.
                }
            }

            if (requiredItem != null) {
                requiredHaveItems.put(requiredItem, amount);
            }
        }

        ConfigurationSection requiredQuestStepSection =
                s.getConfigurationSection("requires_quest_step");

        String requiredQuestId = null;
        int requiredQuestStep = -1;

        if (requiredQuestStepSection != null) {
            requiredQuestId = requiredQuestStepSection.getString("quest", "");
            requiredQuestStep= requiredQuestStepSection.getInt("minimum", -1);
        }

        String displayName = s.getString("display_name", null);
        String icon        = s.getString("icon", "◈");
        int priority       = s.getInt("priority", 50);

        return new NpcStep(type, messages, inProgress, onComplete, delayTicks, mobName, requiredKills, rewards,
                targetNpcId, displayName, icon, priority, permission, skill, requiredSkillLevel, requiredLevel,
                handOverItem, haveMatItem, handOverMatAmount, haveMatAmount,
                requiredHaveItems, requiredTalkedToNpcId, requiredQuestId, requiredQuestStep);
    }

    public record RewardItem(Material material, String customItemKey, int amount) {}
}
