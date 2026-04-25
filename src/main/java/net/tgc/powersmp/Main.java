package net.tgc.powersmp;

import net.tgc.powersmp.ability.*;
import net.tgc.powersmp.api.PowerSMPAPI;
import net.tgc.powersmp.api.command.ApiCommand;
import net.tgc.powersmp.api.item.CustomItemRegistry;
import net.tgc.powersmp.command.HeartsCommand;
import net.tgc.powersmp.command.PowerBarCommand;
import net.tgc.powersmp.command.PowerCommand;
import net.tgc.powersmp.gems.EscapeGem;
import net.tgc.powersmp.gems.NoCooldownGem;
import net.tgc.powersmp.item.AspectOfTheEnd;
import net.tgc.powersmp.item.FireballItem;
import net.tgc.powersmp.item.PowerDiamondItem;
import net.tgc.powersmp.item.PowerEmeraldItem;
import net.tgc.powersmp.item.materials.KineticShard;
import net.tgc.powersmp.item.materials.MaterialDropListener;
import net.tgc.powersmp.item.materials.PrimalEmber;
import net.tgc.powersmp.item.materials.ResoniteCrystal;
import net.tgc.powersmp.item.materials.VoidFragment;
import net.tgc.powersmp.listener.DeathListener;
import net.tgc.powersmp.listener.GemApplicationListener;
import net.tgc.powersmp.listener.ItemUseListener;
import net.tgc.powersmp.listener.PlayerJoinListener;
import net.tgc.powersmp.listener.PowerBarTask;
import net.tgc.powersmp.listener.PowerRegenTask;
import net.tgc.powersmp.player.PlayerDataManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandMap;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        // Init singletons
        PlayerDataManager.getInstance().init(this);
        CustomItemRegistry.getInstance().setPlugin(this);

        // Listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new ItemUseListener(), this);
        getServer().getPluginManager().registerEvents(new GemApplicationListener(), this);
        getServer().getPluginManager().registerEvents(new MaterialDropListener(this), this);

        // Recurring tasks
        PowerBarTask.start(this);
        PowerRegenTask.start(this);

        // Built-in commands
        HeartsCommand heartsCmd = new HeartsCommand();
        PowerCommand  powerCmd  = new PowerCommand();

        CommandMap commandMap = getServer().getCommandMap();
        commandMap.register("powersmp", new ApiCommand(this, "hearts",   heartsCmd,           heartsCmd));
        commandMap.register("powersmp", new ApiCommand(this, "power",    powerCmd,            powerCmd));
        commandMap.register("powersmp", new ApiCommand(this, "powerbar", new PowerBarCommand(), null));

        // --- Materials ---
        KineticShard   kineticShard    = new KineticShard(this);
        VoidFragment   voidFragment    = new VoidFragment(this);
        ResoniteCrystal resoniteCrystal = new ResoniteCrystal(this);
        PrimalEmber    primalEmber     = new PrimalEmber(this);

        PowerSMPAPI.registerItem(kineticShard);
        PowerSMPAPI.registerItem(voidFragment);
        PowerSMPAPI.registerItem(resoniteCrystal);
        PowerSMPAPI.registerItem(primalEmber);

        // Resonite Crystal recipe:  . A .  (A = Amethyst Shard, E = Echo Shard)
        //                           A E A
        //                           . A .
        NamespacedKey resoniteKey = resoniteCrystal.getKey();
        ShapedRecipe resoniteRecipe = new ShapedRecipe(resoniteKey, CustomItemRegistry.getInstance().createItemStack(resoniteKey));
        resoniteRecipe.shape(" A ", "AEA", " A ");
        resoniteRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        resoniteRecipe.setIngredient('E', Material.ECHO_SHARD);
        PowerSMPAPI.registerRecipe(resoniteRecipe);

        // --- Built-in items & abilities ---
        PowerEmeraldItem emeraldItem  = new PowerEmeraldItem(this);
        PowerDiamondItem diamondItem  = new PowerDiamondItem(this);
        NoCooldownGem noCooldownGem = new NoCooldownGem(this);
        FireballItem fireballItem = new FireballItem(this);
        EscapeGem escapeGem = new EscapeGem(this);
        AspectOfTheEnd aspectOfTheEnd = new AspectOfTheEnd(this);

        PowerSMPAPI.registerAbility(new EmeraldDashAbility());
        PowerSMPAPI.registerAbility(new DiamondDashAbility());
        PowerSMPAPI.registerAbility(new FireballAbility());
        PowerSMPAPI.registerAbility(new NoCooldownAbility(this));
        PowerSMPAPI.registerAbility(new EscapeAbility(this));
        PowerSMPAPI.registerItem(emeraldItem);
        PowerSMPAPI.registerItem(diamondItem);
        PowerSMPAPI.registerItem(fireballItem);
        PowerSMPAPI.registerItem(noCooldownGem);
        PowerSMPAPI.registerItem(escapeGem);
        PowerSMPAPI.registerItem(aspectOfTheEnd);

        // Pre-build material stacks for ExactChoice matching.
        // ExactChoice ensures players must use the actual custom material, not vanilla items.
        RecipeChoice kineticChoice  = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(kineticShard.getKey()));
        RecipeChoice voidChoice     = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(voidFragment.getKey()));
        RecipeChoice resoniteChoice = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(resoniteCrystal.getKey()));
        RecipeChoice emberChoice    = new RecipeChoice.ExactChoice(CustomItemRegistry.getInstance().createItemStack(primalEmber.getKey()));

        // --- Crafting recipes ---

        // Power Emerald gem  — kinetic energy   → needs Kinetic Shard
        //   . E .
        //   E K E    E = Emerald  K = Kinetic Shard
        //   . E .
        NamespacedKey emeraldKey = emeraldItem.getKey();
        ShapedRecipe emeraldRecipe = new ShapedRecipe(emeraldKey, CustomItemRegistry.getInstance().createItemStack(emeraldKey));
        emeraldRecipe.shape(" E ", "EKE", " E ");
        emeraldRecipe.setIngredient('E', Material.EMERALD);
        emeraldRecipe.setIngredient('K', kineticChoice);
        PowerSMPAPI.registerRecipe(emeraldRecipe);

        // Power Diamond gem  — warden resonance  → needs Resonite Crystal
        //   . D .
        //   D R D    D = Diamond  R = Resonite Crystal
        //   . D .
        NamespacedKey diamondKey = diamondItem.getKey();
        ShapedRecipe diamondRecipe = new ShapedRecipe(diamondKey, CustomItemRegistry.getInstance().createItemStack(diamondKey));
        diamondRecipe.shape(" D ", "DRD", " D ");
        diamondRecipe.setIngredient('D', Material.DIAMOND);
        diamondRecipe.setIngredient('R', resoniteChoice);
        PowerSMPAPI.registerRecipe(diamondRecipe);
        // Note: applying gems to swords is handled by GemApplicationListener

        // Fireball Item      — primal fire        → needs Primal Ember
        //   P M P
        //   M F M    P = Primal Ember  M = Magma Block  F = Fire Charge
        //   P M P
        NamespacedKey fireBallKey = fireballItem.getKey();
        ShapedRecipe fireBallRecipe = new ShapedRecipe(fireBallKey, CustomItemRegistry.getInstance().createItemStack(fireBallKey));
        fireBallRecipe.shape("PMP", "MFM", "PMP");
        fireBallRecipe.setIngredient('P', emberChoice);
        fireBallRecipe.setIngredient('M', Material.MAGMA_BLOCK);
        fireBallRecipe.setIngredient('F', Material.FIRE_CHARGE);
        PowerSMPAPI.registerRecipe(fireBallRecipe);

        // NoCooldown Gem     — resonant frequency → needs Resonite Crystal (expensive, very powerful)
        //   R D R
        //   D N D    R = Resonite Crystal  D = Diamond  N = Nether Star
        //   R D R
        NamespacedKey noCooldownGemKey = noCooldownGem.getKey();
        ShapedRecipe noCooldownGemRecipe = new ShapedRecipe(noCooldownGemKey, CustomItemRegistry.getInstance().createItemStack(noCooldownGemKey));
        noCooldownGemRecipe.shape("RDR", "DND", "RDR");
        noCooldownGemRecipe.setIngredient('R', resoniteChoice);
        noCooldownGemRecipe.setIngredient('D', Material.DIAMOND);
        noCooldownGemRecipe.setIngredient('N', Material.NETHER_STAR);
        PowerSMPAPI.registerRecipe(noCooldownGemRecipe);

        // Escape Gem         — kinetic burst escape → needs Kinetic Shard (+ vanilla Nether Star)
        //   G . G
        //   K N K    G = Glowstone Dust  K = Kinetic Shard  N = Nether Star
        //   G . G
        NamespacedKey escapeGemKey = escapeGem.getKey();
        ShapedRecipe escapeGemRecipe = new ShapedRecipe(escapeGemKey, CustomItemRegistry.getInstance().createItemStack(escapeGemKey));
        escapeGemRecipe.shape("G G", "KNK", "G G");
        escapeGemRecipe.setIngredient('G', Material.GLOWSTONE_DUST);
        escapeGemRecipe.setIngredient('K', kineticChoice);
        escapeGemRecipe.setIngredient('N', Material.NETHER_STAR);
        PowerSMPAPI.registerRecipe(escapeGemRecipe);

        // Aspect of the End  — void teleport       → needs Void Fragment (premium recipe)
        //   P V P
        //   V E V    P = Ender Pearl  V = Void Fragment  E = Eye of Ender
        //   P V P
        NamespacedKey aspectOfTheEndKey = aspectOfTheEnd.getKey();
        ShapedRecipe aOTErecipe = new ShapedRecipe(aspectOfTheEndKey, CustomItemRegistry.getInstance().createItemStack(aspectOfTheEndKey));
        aOTErecipe.shape("PVP", "VEV", "PVP");
        aOTErecipe.setIngredient('P', Material.ENDER_PEARL);
        aOTErecipe.setIngredient('V', voidChoice);
        aOTErecipe.setIngredient('E', Material.ENDER_EYE);
        PowerSMPAPI.registerRecipe(aOTErecipe);

        getLogger().info("PowerSMP API enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PowerSMP API disabled.");
    }

    public static Main getInstance() { return instance; }
}
