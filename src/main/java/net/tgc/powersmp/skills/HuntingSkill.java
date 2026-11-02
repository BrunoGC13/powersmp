package net.tgc.powersmp.skills;

import net.tgc.powersmp.Main;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class HuntingSkill implements Listener {
    private Main plugin;
    private final Set<EntityType> entities = new HashSet<>(Arrays.asList(
            EntityType.SHEEP,
            EntityType.COW,
            EntityType.PIG,
            EntityType.RABBIT,
            EntityType.ZOMBIE,
            EntityType.SPIDER,
            EntityType.SKELETON,
            EntityType.BREEZE,
            EntityType.CREAKING,
            EntityType.CREEPER,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.SILVERFISH,
            EntityType.BLAZE,
            EntityType.WITHER_SKELETON,
            EntityType.WITCH,
            EntityType.WARDEN,
            EntityType.WITHER,
            EntityType.COD,
            EntityType.DOLPHIN,
            EntityType.SALMON,
            EntityType.ELDER_GUARDIAN,
            EntityType.DROWNED,
            EntityType.GUARDIAN,
            EntityType.EVOKER,
            EntityType.VINDICATOR,
            EntityType.PILLAGER,
            EntityType.GHAST,
            EntityType.MAGMA_CUBE,
            EntityType.PHANTOM,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.ZOMBIFIED_PIGLIN,
            EntityType.CAVE_SPIDER,
            EntityType.ZOGLIN,
            EntityType.RAVAGER,
            EntityType.SHULKER,
            EntityType.SLIME,
            EntityType.STRAY,
            EntityType.VEX,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_NAUTILUS,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.SKELETON_HORSE
    ));

    private Skill huntingSkill;

    public HuntingSkill(Main plugin) {
        this.plugin = plugin;
        this.huntingSkill = plugin.getSkillsRegistry().getSkillData("hunting_skill");
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent e) {
        if (!(entities.contains(e.getEntityType()))) return;

        if (e.getDamageSource().getCausingEntity() instanceof Player p) {
            plugin.getSkillsManager().addProgress(1, p, huntingSkill);
        }
    }
}
