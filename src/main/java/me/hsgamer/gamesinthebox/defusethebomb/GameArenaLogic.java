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
import me.hsgamer.hscore.bukkit.scheduler.Scheduler;
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
    }

    @Override
    public void postInit() {
        GameConfigFeature configFeature = arena.getFeature(GameConfigFeature.class);

        maxSpawn = Optional.ofNullable(configFeature.getString("tnt.max-spawn"))
                .flatMap(Validate::getNumber)
                .map(Number::intValue)
                .orElse(maxSpawn);
    }

    @Override
    public List<Feature> loadFeatures() {
        SimpleBoundingFeature boundingFeature = new SimpleBoundingFeature(arena, true);
        return Arrays.asList(
                boundingFeature,
                new SimpleBoundingOffsetFeature(arena, boundingFeature),
                new TntFeature(arena),
                new ListenerFeature(expansion, arena)
        );
    }

    @Override
    public void onInGameStart() {
        arena.getFeature(ListenerFeature.class).register();
    }

    @Override
    public void onInGameUpdate() {
        BoundingFeature boundingFeature = arena.getFeature(BoundingFeature.class);
        BoundingOffsetFeature boundingOffsetFeature = arena.getFeature(BoundingOffsetFeature.class);
        TntFeature tntFeature = arena.getFeature(TntFeature.class);

        long size = tntFeature.countValid();

        if (size < maxSpawn) {
            tntFeature.spawn(boundingOffsetFeature.getRandomLocation());
        }

        tntFeature.streamValid()
                .filter(tnt -> !boundingFeature.checkBounding(tnt.getLocation()))
                .forEach(tnt -> Scheduler.CURRENT.runEntityTask(expansion.getPlugin(), tnt, tnt::remove, () -> {
                }, false));
    }

    @Override
    public void onInGameOver() {
        arena.getFeature(ListenerFeature.class).unregister();
    }

    @Override
    public void onEndingStart() {
        List<UUID> topList = arena.getFeature(PointFeature.class).getTopUUID().collect(Collectors.toList());
        arena.getFeature(SimpleRewardFeature.class).tryReward(topList);
    }
}
