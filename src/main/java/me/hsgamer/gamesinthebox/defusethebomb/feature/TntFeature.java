package me.hsgamer.gamesinthebox.defusethebomb.feature;

import me.hsgamer.gamesinthebox.game.feature.EntityFeature;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

public class TntFeature extends EntityFeature {
    private int minFuseTicks = 20;
    private int maxFuseTicks = 40;
    private float explodeYield = 4.0f;

    @Override
    protected @Nullable Entity createEntity(Location location) {
        World world = location.getWorld();
        assert world != null;
        if (!world.getChunkAt(location).isLoaded())
            return null;
        return world.spawn(location, TNTPrimed.class, tnt -> {
            tnt.setFuseTicks(ThreadLocalRandom.current().nextInt(this.minFuseTicks, this.maxFuseTicks + 1));
            tnt.setYield(this.explodeYield);
            tnt.setIsIncendiary(false);
            tnt.setCustomNameVisible(true);
//            tnt.setCustomName(getRandomNameTag());
            tnt.setGlowing(true);
        });
    }
}
