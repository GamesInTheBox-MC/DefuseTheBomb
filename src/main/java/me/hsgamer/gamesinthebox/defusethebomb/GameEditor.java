package me.hsgamer.gamesinthebox.defusethebomb;

import me.hsgamer.gamesinthebox.defusethebomb.feature.ListenerFeature;
import me.hsgamer.gamesinthebox.defusethebomb.feature.TntFeature;
import me.hsgamer.gamesinthebox.game.GameArena;
import me.hsgamer.gamesinthebox.game.simple.action.NumberAction;
import me.hsgamer.gamesinthebox.game.simple.action.ValueAction;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingFeature;
import me.hsgamer.gamesinthebox.game.simple.feature.SimpleBoundingOffsetFeature;
import me.hsgamer.gamesinthebox.game.template.TemplateGame;
import me.hsgamer.gamesinthebox.game.template.TemplateGameArenaLogic;
import me.hsgamer.gamesinthebox.game.template.TemplateGameEditor;
import me.hsgamer.gamesinthebox.game.template.feature.ArenaLogicFeature;
import me.hsgamer.hscore.bukkit.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GameEditor extends TemplateGameEditor {
    private final SimpleBoundingFeature.Editor simpleBoundingFeatureEditor = SimpleBoundingFeature.editor(true);
    private final SimpleBoundingOffsetFeature.Editor simpleBoundingOffsetFeatureEditor = SimpleBoundingOffsetFeature.editor();
    private final List<String> nameTags = new ArrayList<>();
    private Integer minFuseTicks;
    private Integer maxFuseTicks;
    private Float explodeYield;
    private Boolean isDamageOnExplode;
    private Integer maxSpawn;

    public GameEditor(@NotNull TemplateGame game) {
        super(game);
    }

    @Override
    protected @NotNull Map<String, SimpleAction> createActionMap() {
        Map<String, SimpleAction> map = super.createActionMap();

        map.putAll(simpleBoundingFeatureEditor.getActions());
        map.putAll(simpleBoundingOffsetFeatureEditor.getActions());

        map.put("set-min-fuse-ticks", new NumberAction() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Number value, String... args) {
                minFuseTicks = value.intValue();
                return true;
            }

            @Override
            public @NotNull String getDescription() {
                return "Set the minimum fuse ticks of the TNT";
            }
        });
        map.put("set-max-fuse-ticks", new NumberAction() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Number value, String... args) {
                maxFuseTicks = value.intValue();
                return true;
            }

            @Override
            public @NotNull String getDescription() {
                return "Set the maximum fuse ticks of the TNT";
            }
        });
        map.put("set-explode-yield", new NumberAction() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Number value, String... args) {
                explodeYield = value.floatValue();
                return true;
            }

            @Override
            public @NotNull String getDescription() {
                return "Set the explode yield of the TNT";
            }
        });
        map.put("set-damage-on-explode", new ValueAction<Boolean>() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Boolean value, String... args) {
                isDamageOnExplode = value;
                return true;
            }

            @Override
            protected int getValueArgCount() {
                return 1;
            }

            @Override
            protected Optional<Boolean> parseValue(@NotNull CommandSender sender, String... args) {
                return Optional.of(Boolean.parseBoolean(args[0]));
            }

            @Override
            protected @NotNull List<String> getValueArgs(@NotNull CommandSender sender, String... args) {
                return Arrays.asList("true", "false");
            }

            @Override
            public @NotNull String getDescription() {
                return "Set whether the TNT will damage the players or not";
            }

            @Override
            public @NotNull String getArgsUsage() {
                return "<true/false>";
            }
        });
        map.put("set-max-spawn", new NumberAction() {
            @Override
            protected boolean performAction(@NotNull CommandSender sender, @NotNull Number value, String... args) {
                maxSpawn = value.intValue();
                return true;
            }

            @Override
            public @NotNull String getDescription() {
                return "Set the maximum number of TNTs that can be spawned";
            }
        });
        map.put("add-name-tag", new SimpleAction() {
            @Override
            public @NotNull String getDescription() {
                return "Add a name tag to the list";
            }

            @Override
            public boolean performAction(@NotNull CommandSender sender, @NotNull String... args) {
                if (args.length == 0) {
                    return false;
                }
                nameTags.add(String.join(" ", args));
                return true;
            }

            @Override
            public @NotNull String getArgsUsage() {
                return "<name tag>";
            }
        });
        map.put("clear-name-tags", new SimpleAction() {
            @Override
            public @NotNull String getDescription() {
                return "Clear the name tags list";
            }

            @Override
            public boolean performAction(@NotNull CommandSender sender, @NotNull String... args) {
                nameTags.clear();
                return true;
            }
        });

        return map;
    }

    @Override
    protected @NotNull List<@NotNull SimpleEditorStatus> createEditorStatusList() {
        List<@NotNull SimpleEditorStatus> list = super.createEditorStatusList();
        list.add(simpleBoundingFeatureEditor.getStatus());
        list.add(simpleBoundingOffsetFeatureEditor.getStatus());
        list.add(new SimpleEditorStatus() {
            @Override
            public void sendStatus(@NotNull CommandSender sender) {
                MessageUtils.sendMessage(sender, "&6&lDefuse the Bomb");
                MessageUtils.sendMessage(sender, "&6Min Fuse Ticks: &f" + (minFuseTicks == null ? "Default" : minFuseTicks));
                MessageUtils.sendMessage(sender, "&6Max Fuse Ticks: &f" + (maxFuseTicks == null ? "Default" : maxFuseTicks));
                MessageUtils.sendMessage(sender, "&6Explode Yield: &f" + (explodeYield == null ? "Default" : explodeYield));
                MessageUtils.sendMessage(sender, "&6Damage on Explode: &f" + (isDamageOnExplode == null ? "Default" : isDamageOnExplode));
                MessageUtils.sendMessage(sender, "&6Max Spawn: &f" + (maxSpawn == null ? "Default" : maxSpawn));
                MessageUtils.sendMessage(sender, "&6Name Tags: ");
                nameTags.forEach(nameTag -> MessageUtils.sendMessage(sender, "&f- " + nameTag));
            }

            @Override
            public void reset(@NotNull CommandSender sender) {
                minFuseTicks = null;
                maxFuseTicks = null;
                explodeYield = null;
                isDamageOnExplode = null;
                maxSpawn = null;
                nameTags.clear();
            }

            @Override
            public boolean canSave(@NotNull CommandSender sender) {
                return true;
            }

            @Override
            public Map<String, Object> toPathValueMap(@NotNull CommandSender sender) {
                Map<String, Object> map = new LinkedHashMap<>();
                if (minFuseTicks != null) {
                    map.put("tnt.min-fuse-ticks", minFuseTicks);
                }
                if (maxFuseTicks != null) {
                    map.put("tnt.max-fuse-ticks", maxFuseTicks);
                }
                if (explodeYield != null) {
                    map.put("tnt.explode-yield", explodeYield);
                }
                if (isDamageOnExplode != null) {
                    map.put("tnt.damage", isDamageOnExplode);
                }
                if (!nameTags.isEmpty()) {
                    map.put("tnt.name-tag", nameTags);
                }
                if (maxSpawn != null) {
                    map.put("tnt.max-spawn", maxSpawn);
                }
                return map;
            }
        });
        return list;
    }

    @Override
    public boolean migrate(@NotNull CommandSender sender, @NotNull GameArena gameArena) {
        ArenaLogicFeature arenaLogicFeature = gameArena.getFeature(ArenaLogicFeature.class);
        if (arenaLogicFeature == null) {
            return false;
        }
        TemplateGameArenaLogic templateGameArenaLogic = arenaLogicFeature.getArenaLogic();
        if (!(templateGameArenaLogic instanceof GameArenaLogic)) {
            return false;
        }
        GameArenaLogic gameArenaLogic = (GameArenaLogic) templateGameArenaLogic;

        TntFeature tntFeature = gameArena.getFeature(TntFeature.class);
        minFuseTicks = tntFeature.getMinFuseTicks();
        maxFuseTicks = tntFeature.getMaxFuseTicks();
        explodeYield = tntFeature.getExplodeYield();
        nameTags.clear();
        nameTags.addAll(tntFeature.getNameTags());

        ListenerFeature listenerFeature = gameArena.getFeature(ListenerFeature.class);
        isDamageOnExplode = listenerFeature.isDamage();

        maxSpawn = gameArenaLogic.getMaxSpawn();
        simpleBoundingFeatureEditor.migrate(gameArena.getFeature(SimpleBoundingFeature.class));
        simpleBoundingOffsetFeatureEditor.migrate(gameArena.getFeature(SimpleBoundingOffsetFeature.class));
        return super.migrate(sender, gameArena);
    }
}
