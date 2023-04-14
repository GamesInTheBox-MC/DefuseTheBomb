package me.hsgamer.gamesinthebox.defusethebomb.feature;

import me.hsgamer.gamesinthebox.game.feature.EntityFeature;
import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.gamesinthebox.util.Util;
import me.hsgamer.hscore.common.CollectionUtils;
import me.hsgamer.hscore.common.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class TntFeature extends EntityFeature {
    private final SimpleGameArena arena;
    private int minFuseTicks = 20;
    private int maxFuseTicks = 40;
    private float explodeYield = 4.0f;
    private List<String> nameTags = Collections.emptyList();

    public TntFeature(SimpleGameArena arena) {
        this.arena = arena;
    }

    @Override
    public void postInit() {
        GameConfigFeature config = arena.getFeature(GameConfigFeature.class);

        minFuseTicks = Optional.ofNullable(config.getString("tnt.min-fuse-ticks"))
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(minFuseTicks);
        maxFuseTicks = Optional.ofNullable(config.getString("tnt.max-fuse-ticks"))
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(maxFuseTicks);
        explodeYield = Optional.ofNullable(config.getString("tnt.explode-yield"))
                .flatMap(Validate::getNumber)
                .map(Number::floatValue)
                .orElse(explodeYield);
        nameTags = Optional.ofNullable(config.get("tnt.name-tag"))
                .map(CollectionUtils::createStringListFromObject)
                .orElse(nameTags);
    }

    @Override
    protected @Nullable Entity createEntity(Location location) {
        return location.getWorld().spawn(location, TNTPrimed.class, tnt -> {
            tnt.setFuseTicks(ThreadLocalRandom.current().nextInt(this.minFuseTicks, this.maxFuseTicks + 1));
            tnt.setYield(this.explodeYield);
            tnt.setIsIncendiary(false);
            tnt.setGlowing(true);

            String nameTag = Util.getRandomColorizedString(nameTags, "");
            if (!nameTag.isEmpty()) {
                tnt.setCustomName(nameTag);
                tnt.setCustomNameVisible(true);
            }
        });
    }

    public int getMinFuseTicks() {
        return minFuseTicks;
    }

    public int getMaxFuseTicks() {
        return maxFuseTicks;
    }

    public float getExplodeYield() {
        return explodeYield;
    }

    public List<String> getNameTags() {
        return nameTags;
    }
}
