package com.hyfactions.enchanter.util;

import com.hypixel.hytale.server.core.world.World;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.math.Position;

/**
 * Utility class for spawning particles, sounds, and visual effects
 * All methods ensure proper world thread execution
 */
public class EffectUtil {

    /**
     * Spawn a particle effect at a location
     * TODO: Implement when Hytale particle API is available
     *
     * @param world    The world
     * @param position The position to spawn at
     * @param particle The particle type
     * @param count    Number of particles
     */
    public static void spawnParticle(World world, Position position, String particle, int count) {
        world.execute(() -> {
            // Note: This will use Hytale's actual particle system
            // Pseudocode for future implementation:
            // world.spawnParticle(ParticleType.valueOf(particle), position, count);
        });
    }

    /**
     * Spawn particles around a player
     *
     * @param player   The player
     * @param particle The particle type
     * @param count    Number of particles
     * @param radius   Radius around player
     */
    public static void spawnParticleAroundPlayer(Player player, String particle, int count, double radius) {
        World world = player.getWorld();
        Position position = player.getPosition();

        world.execute(() -> {
            // Spawn particles in a circle around the player
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI * i) / count;
                double x = position.getX() + radius * Math.cos(angle);
                double z = position.getZ() + radius * Math.sin(angle);
                Position particlePos = new Position(x, position.getY() + 1, z);

                // world.spawnParticle(ParticleType.valueOf(particle), particlePos, 1);
            }
        });
    }

    /**
     * Play a sound at a location
     * TODO: Implement when Hytale sound API is available
     *
     * @param world    The world
     * @param position The position to play at
     * @param sound    The sound name
     * @param volume   Sound volume (0.0-1.0)
     * @param pitch    Sound pitch (0.5-2.0)
     */
    public static void playSound(World world, Position position, String sound, float volume, float pitch) {
        world.execute(() -> {
            // Note: This will use Hytale's actual sound system
            // Pseudocode for future implementation:
            // world.playSound(position, sound, volume, pitch);
        });
    }

    /**
     * Play a sound for a specific player
     *
     * @param player The player
     * @param sound  The sound name
     * @param volume Sound volume
     * @param pitch  Sound pitch
     */
    public static void playSoundForPlayer(Player player, String sound, float volume, float pitch) {
        playSound(player.getWorld(), player.getPosition(), sound, volume, pitch);
    }

    /**
     * Strike lightning at a position
     * TODO: Implement when Hytale weather/lightning API is available
     *
     * @param world    The world
     * @param position The position to strike
     */
    public static void strikeLightning(World world, Position position) {
        world.execute(() -> {
            // Note: This will use Hytale's actual lightning system
            // Pseudocode for future implementation:
            // world.strikeLightning(position);

            // For now, just create visual effect
            spawnParticle(world, position, "FIREWORKS_SPARK", 20);
        });
    }

    /**
     * Create an explosion effect (visual only, no damage)
     *
     * @param world    The world
     * @param position The position
     * @param radius   Explosion radius for particles
     */
    public static void createExplosion(World world, Position position, double radius) {
        world.execute(() -> {
            // Spawn explosion particles
            spawnParticle(world, position, "EXPLOSION_LARGE", (int) (radius * 10));
            playSound(world, position, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        });
    }

    /**
     * Create a magical aura effect around a position
     *
     * @param world    The world
     * @param position The center position
     * @param particle The particle type
     * @param radius   The radius of the aura
     */
    public static void createAuraEffect(World world, Position position, String particle, double radius) {
        world.execute(() -> {
            // Create a circular aura of particles
            int particleCount = (int) (radius * 8);
            for (int i = 0; i < particleCount; i++) {
                double angle = (2 * Math.PI * i) / particleCount;
                double x = position.getX() + radius * Math.cos(angle);
                double z = position.getZ() + radius * Math.sin(angle);

                for (int y = 0; y < 3; y++) {
                    Position particlePos = new Position(x, position.getY() + y * 0.5, z);
                    spawnParticle(world, particlePos, particle, 1);
                }
            }
        });
    }

    /**
     * Create a beam effect between two positions
     *
     * @param world  The world
     * @param start  Start position
     * @param end    End position
     * @param particle Particle type
     */
    public static void createBeamEffect(World world, Position start, Position end, String particle) {
        world.execute(() -> {
            // Calculate distance and direction
            double distance = start.distance(end);
            int steps = (int) (distance * 5); // 5 particles per block

            for (int i = 0; i <= steps; i++) {
                double ratio = (double) i / steps;
                double x = start.getX() + (end.getX() - start.getX()) * ratio;
                double y = start.getY() + (end.getY() - start.getY()) * ratio;
                double z = start.getZ() + (end.getZ() - start.getZ()) * ratio;

                Position particlePos = new Position(x, y, z);
                spawnParticle(world, particlePos, particle, 1);
            }
        });
    }

    /**
     * Create a spiral effect
     *
     * @param world    The world
     * @param position Center position
     * @param particle Particle type
     * @param height   Height of the spiral
     * @param radius   Radius of the spiral
     */
    public static void createSpiralEffect(World world, Position position, String particle, double height, double radius) {
        world.execute(() -> {
            int steps = 50;
            for (int i = 0; i < steps; i++) {
                double ratio = (double) i / steps;
                double angle = ratio * Math.PI * 4; // 2 full rotations
                double y = position.getY() + (height * ratio);
                double x = position.getX() + radius * Math.cos(angle) * (1 - ratio);
                double z = position.getZ() + radius * Math.sin(angle) * (1 - ratio);

                Position particlePos = new Position(x, y, z);
                spawnParticle(world, particlePos, particle, 1);
            }
        });
    }
}
