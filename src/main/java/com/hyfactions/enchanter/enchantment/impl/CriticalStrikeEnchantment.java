package com.hyfactions.enchanter.enchantment.impl;

import com.hyfactions.enchanter.enchantment.*;
import java.util.*;

public class CriticalStrikeEnchantment implements EnchantmentHandler {
    private final CustomEnchantment enchantment;

    public CriticalStrikeEnchantment() {
        Map<Integer, CustomEnchantment.EnchantmentLevel> levels = new HashMap<>();
        levels.put(1, CustomEnchantment.EnchantmentLevel.builder().level(1).cost(8000)
                .properties(Map.of("crit-chance", 10, "crit-multiplier", 1.5)).build());
        levels.put(2, CustomEnchantment.EnchantmentLevel.builder().level(2).cost(16000)
                .properties(Map.of("crit-chance", 15, "crit-multiplier", 1.75)).build());
        levels.put(3, CustomEnchantment.EnchantmentLevel.builder().level(3).cost(30000)
                .properties(Map.of("crit-chance", 20, "crit-multiplier", 2.0)).build());
        levels.put(4, CustomEnchantment.EnchantmentLevel.builder().level(4).cost(50000)
                .properties(Map.of("crit-chance", 25, "crit-multiplier", 2.25)).build());
        levels.put(5, CustomEnchantment.EnchantmentLevel.builder().level(5).cost(80000)
                .properties(Map.of("crit-chance", 30, "crit-multiplier", 2.5)).build());

        this.enchantment = CustomEnchantment.builder().id("CRITICAL_STRIKE")
                .displayName("&6Critical Strike").tier(EnchantmentTier.EPIC)
                .category(EnchantmentCategory.COMBAT).type(EnchantmentType.WEAPON)
                .maxLevel(5).levels(levels)
                .description(Arrays.asList("&7Chance to deal bonus damage", "&7with critical hits"))
                .applicableItem("SWORD").applicableItem("AXE").applicableItem("BOW")
                .synergy("SHARPNESS_BOOST").particle("CRIT").sound("ENTITY_ARROW_HIT_PLAYER").build();
    }

    @Override
    public CustomEnchantment getEnchantment() { return enchantment; }
}
