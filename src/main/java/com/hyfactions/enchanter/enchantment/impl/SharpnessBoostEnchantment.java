package com.hyfactions.enchanter.enchantment.impl;
import com.hyfactions.enchanter.enchantment.*;
import java.util.*;

public class SharpnessBoostEnchantment implements EnchantmentHandler {
    private final CustomEnchantment enchantment;
    public SharpnessBoostEnchantment() {
        Map<Integer, CustomEnchantment.EnchantmentLevel> levels = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            levels.put(i, CustomEnchantment.EnchantmentLevel.builder().level(i).cost(2000 * i)
                    .properties(Map.of("damage-bonus", (double) i)).build());
        }
        this.enchantment = CustomEnchantment.builder().id("SHARPNESS_BOOST").displayName("&eSharpness Boost")
                .tier(EnchantmentTier.COMMON).category(EnchantmentCategory.COMBAT).type(EnchantmentType.WEAPON)
                .maxLevel(5).levels(levels).description(Arrays.asList("&7Increases weapon damage"))
                .applicableItem("SWORD").applicableItem("AXE").particle("SWEEP_ATTACK").sound("ITEM_SWORD_HIT").build();
    }
    @Override
    public CustomEnchantment getEnchantment() { return enchantment; }
}
