package com.hyfactions.enchanter.ability;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Represents an effect that an ability can produce
 */
@Data
@Builder
public class AbilityEffect {
    private final EffectType type;
    private final Map<String, Object> properties;

    public <T> T getProperty(String key, Class<T> clazz, T defaultValue) {
        if (properties == null || !properties.containsKey(key)) {
            return defaultValue;
        }
        try {
            return clazz.cast(properties.get(key));
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public enum EffectType {
        LIGHTNING, PARTICLE, TELEPORT, INVISIBILITY, POTION,
        HEAL, FIREBALL, FIRE, FREEZE, PROJECTILE,
        HEAL_ON_HIT, AREA_DAMAGE, CHAIN_DAMAGE, DAMAGE_ABSORB,
        BONUS_DROPS, REVIVE, MULTI_ELEMENT, DAMAGE_REDUCTION, REFLECT_DAMAGE
    }
}
