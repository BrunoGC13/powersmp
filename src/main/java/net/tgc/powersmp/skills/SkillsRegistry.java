package net.tgc.powersmp.skills;

import net.tgc.powersmp.Main;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SkillsRegistry {
    private Main plugin;
    public SkillsRegistry(Main plugin) {
        this.plugin = plugin;
    }
    public Skill getSkillData(String name) {
        ConfigurationSection skills = plugin.getSkillsData().getConfigurationSection("skills");
        if (skills == null) return null;

        for (String treeKey : skills.getKeys(false)) {
            ConfigurationSection skill = plugin.getSkillsData().getConfigurationSection("skills." + treeKey);
            assert skill != null;
            if (Objects.equals(skill.getString("name"), name)) {
                return new Skill(skill.getString("name"), skill.getInt("target"));
            }
        }
        return null;

    }
    public void registerSkill(Skill skill) {
        String base = "skills." + skill.getName() + ".";
        plugin.getSkillsData().set(base + "target", skill.getTarget());
        plugin.getSkillsData().set(base + "name", skill.getName());
        plugin.saveSkillsData();
    }

    public List<Skill> getAllSkills() {
        List<Skill> result = new ArrayList<>();
        ConfigurationSection skills = plugin.getSkillsData().getConfigurationSection("skills");
        if (skills == null) return result;
        for (String key : skills.getKeys(false)) {
            ConfigurationSection s = skills.getConfigurationSection(key);
            if (s != null) result.add(new Skill(s.getString("name"), s.getInt("target")));
        }
        return result;
    }
}
