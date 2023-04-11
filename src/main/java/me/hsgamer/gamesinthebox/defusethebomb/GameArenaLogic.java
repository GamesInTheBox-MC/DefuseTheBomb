package me.hsgamer.gamesinthebox.defusethebomb;

import me.hsgamer.gamesinthebox.defusethebomb.feature.TntFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArena;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.minigamecore.base.Feature;

import java.util.Arrays;
import java.util.List;

public class GameArenaLogic extends TemplateGameArenaLogic {
    public GameArenaLogic(TemplateGameArena arena) {
        super(arena);
    }

    @Override
    public List<Feature> loadFeatures() {
        SimpleBoundingFeature boundingFeature = new SimpleBoundingFeature(arena, true);
        return Arrays.asList(
                boundingFeature,
                new SimpleBoundingOffsetFeature(arena, boundingFeature),
                new TntFeature()
        );
    }
}
