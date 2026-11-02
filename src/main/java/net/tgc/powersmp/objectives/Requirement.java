package net.tgc.powersmp.objectives;

import net.tgc.powersmp.skills.Skill;
import org.bukkit.inventory.ItemStack;

public class Requirement {
    private RequirementType type;

    private int requiredSkillLevel;
    private Skill skill;

    private int requiredLevel;

    private ItemStack requiredHandItem;
    private ItemStack requiredHaveItem;

    public Requirement(RequirementType type, int requiredSkillLevel, Skill skill) {
        this.type = type;
        this.requiredSkillLevel = requiredSkillLevel;
        this.skill = skill;
    }

    public Requirement(RequirementType type, int requiredLevel) {
        this.type = type;
        this.requiredLevel = requiredLevel;
    }

    public Requirement(RequirementType type, ItemStack item) {
        this.type = type;

        switch (type) {
            case HAND_ITEM:
                this.requiredHandItem = item;
                break;

            case HAVE_ITEM:
                this.requiredHaveItem = item;
                break;
        }
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public int getRequiredSkillLevel() {
        return requiredSkillLevel;
    }

    public ItemStack getRequiredHandItem() {
        return requiredHandItem;
    }

    public ItemStack getRequiredHaveItem() {
        return requiredHaveItem;
    }

    public RequirementType getType() {
        return type;
    }

    public Skill getSkill() {
        return skill;
    }
}
