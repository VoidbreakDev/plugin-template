package com.hyfactions.enchanter.enchantment.impl;
import com.hyfactions.enchanter.enchantment.*;
import java.util.*;

public class IronSkinEnchantment implements EnchantmentHandler {
    private final CustomEnchantment enchantment;
    public IronSkinEnchantment() {
        // Stub implementation - configure based on enchantments.yml
        this.enchantment = CustomEnchantment.builder()
                .id("IronSkin")
                .displayName("&7IronSkin")
                .tier(EnchantmentTier.COMMON)
                .category(EnchantmentCategory.COMBAT)
                .type(EnchantmentType.WEAPON)
                .maxLevel(1)
                .levels(Map.of(1, CustomEnchantment.EnchantmentLevel.builder().level(1).cost(1000).properties(Map.of()).build()))
                .description(Arrays.asList("&7Placeholder enchantment"))
                .build();
    }
    @Override
    public CustomEnchantment getEnchantment() { return enchantment; }
}
