package me.hsgamer.gamesinthebox.defusethebomb;

import me.hsgamer.gamesinthebox.defusethebomb.feature.ListenerFeature;
import me.hsgamer.gamesinthebox.defusethebomb.feature.TntFeature;
import me.hsgamer.gamesinthebox.game.feature.BoundingFeature;
import me.hsgamer.gamesinthebox.game.feature.BoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.feature.PointFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleRewardFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArena;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.hscore.common.Validate;
import me.hsgamer.minigamecore.base.Feature;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameArenaLogic extends TemplateGameArenaLogic {
    private final DefuseTheBomb expansion;
    private int maxSpawn = 10;

    public GameArenaLogic(DefuseTheBomb expansion, TemplateGameArena arena) {
        super(arena);
        this.expansion = expansion;
    }

    public int getMaxSpawn() {
        return maxSpawn;
    }

    @Override
    public void forceEnd() {
        arena.getFeature(ListenerFeature.class).unregister();
        arena.getFeature(TntFeature.class).setClearAllEntities(false);
        arena.getFeature(TntFeature.class).stopClearEntities();
        arena.getFeature(TntFeature.class).clearAllEntities();
    }

    @Override
    public void postInit() {
        GameConfigFeature configFeature = arena.getFeature(GameConfigFeature.class);

        maxSpawn = Optional.ofNullable(configFeature.getString("tnt.max-spawn"))
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(maxSpawn);

        BoundingFeature boundingFeature = arena.getFeature(BoundingFeature.class);
        arena.getFeature(TntFeature.class).addEntityClearCheck(entity -> !boundingFeature.checkBounding(entity.getLocation(), true));
    }

    @Override
    public List<Feature> loadFeatures() {
        SimpleBoundingFeature boundingFeature = new SimpleBoundingFeature(arena);
        return Arrays.asList(
                boundingFeature,
                new SimpleBoundingOffsetFeature(arena, boundingFeature),
                new TntFeature(arena),
                new ListenerFeature(expansion, arena, this)
        );
    }

    @Override
    public void onInGameStart() {
        arena.getFeature(ListenerFeature.class).register();
        arena.getFeature(TntFeature.class).startClearEntities();
    }

    @Override
    public void onInGameUpdate() {
        BoundingOffsetFeature boundingOffsetFeature = arena.getFeature(BoundingOffsetFeature.class);
        TntFeature tntFeature = arena.getFeature(TntFeature.class);

        long size = tntFeature.countValid();

        long toSpawn = maxSpawn - (int) size;
        for (int i = 0; i < toSpawn; i++) {
            tntFeature.spawn(boundingOffsetFeature.getRandomLocation());
        }
    }

    @Override
    public void onEndingStart() {
        List<UUID> topList = arena.getFeature(PointFeature.class).getTopUUID().collect(Collectors.toList());
        arena.getFeature(SimpleRewardFeature.class).tryReward(topList);

        arena.getFeature(TntFeature.class).setClearAllEntities(true);
    }

    @Override
    public boolean isEndingOver() {
        return super.isEndingOver() && arena.getFeature(TntFeature.class).isAllEntityCleared();
    }

    @Override
    public void onEndingOver() {
        arena.getFeature(ListenerFeature.class).unregister();
        arena.getFeature(TntFeature.class).setClearAllEntities(false);
        arena.getFeature(TntFeature.class).stopClearEntities();
    }
}
