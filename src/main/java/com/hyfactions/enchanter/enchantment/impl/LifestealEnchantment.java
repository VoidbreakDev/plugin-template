package com.hyfactions.enchanter.enchantment.impl;

import com.hyfactions.enchanter.enchantment.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Lifesteal enchantment implementation
 * Heals the player for a percentage of damage dealt
 */
public class LifestealEnchantment implements EnchantmentHandler {

    private final CustomEnchantment enchantment;

    public LifestealEnchantment() {
        // Build level configurations
        Map<Integer, CustomEnchantment.EnchantmentLevel> levels = new HashMap<>();

        levels.put(1, CustomEnchantment.EnchantmentLevel.builder()
                .level(1)
                .cost(5000)
                .properties(Map.of("heal-percent", 10))
                .build());

        levels.put(2, CustomEnchantment.EnchantmentLevel.builder()
                .level(2)
                .cost(12000)
                .properties(Map.of("heal-percent", 20))
                .build());

        levels.put(3, CustomEnchantment.EnchantmentLevel.builder()
                .level(3)
                .cost(25000)
                .properties(Map.of("heal-percent", 30))
                .build());

        // Build enchantment
        this.enchantment = CustomEnchantment.builder()
                .id("LIFESTEAL")
                .displayName("&cLifesteal")
                .description(Arrays.asList(
                        "&7Heal for a percentage of damage dealt",
                        "&7to enemies"
                ))
                .tier(EnchantmentTier.RARE)
                .category(EnchantmentCategory.COMBAT)
                .type(EnchantmentType.WEAPON)
                .maxLevel(3)
                .levels(levels)
                .applicableItem("SWORD")
                .applicableItem("AXE")
                .conflict("POISON_STRIKE")
                .particle("HEART")
                .sound("ENTITY_PLAYER_LEVELUP")
                .build();
    }

    @Override
    public CustomEnchantment getEnchantment() {
        return enchantment;
    }

    @Override
    public boolean onApply(EnchantmentContext context) {
        // Validation could go here
        return true;
    }

    /**
     * This would be called from CombatListener when damage is dealt
     * Implementation depends on Hytale API event structure
     */
    public void handleDamage(EnchantmentContext context, double damage) {
        // Get heal percentage from enchantment level
        CustomEnchantment.EnchantmentLevel level = enchantment.getLevel(context.getLevel());
        if (level == null) return;

        int healPercent = level.getIntProperty("heal-percent", 0);

        // Calculate heal amount
        double healAmount = damage * (healPercent / 100.0);

        // Heal the player
        // In actual implementation with Hytale API:
        // Player player = context.getPlayer(Player.class);
        // if (player != null) {
        //     double newHealth = Math.min(player.getMaxHealth(), player.getHealth() + healAmount);
        //     player.setHealth(newHealth);
        //
        //     // Play particle effect
        //     player.getWorld().spawnParticle(Particle.HEART, player.getLocation(), 5);
        // }

        context.setData("heal-amount", healAmount);
    }
}
