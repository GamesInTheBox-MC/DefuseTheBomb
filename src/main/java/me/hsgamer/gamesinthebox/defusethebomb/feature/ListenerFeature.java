package me.hsgamer.gamesinthebox.defusethebomb.feature;

import me.hsgamer.gamesinthebox.defusethebomb.DefuseTheBomb;
import me.hsgamer.gamesinthebox.defusethebomb.GameArenaLogic;
import me.hsgamer.gamesinthebox.game.feature.GameConfigFeature;
import me.hsgamer.gamesinthebox.game.simple.SimpleGameArena;
import me.hsgamer.gamesinthebox.game.simple.feature.SimplePointFeature;
import me.hsgamer.gamesinthebox.util.EntityUtil;
import me.hsgamer.minigamecore.base.Feature;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Optional;

public class ListenerFeature implements Feature, Listener {
    private final DefuseTheBomb expansion;
    private final SimpleGameArena arena;
    private final GameArenaLogic arenaLogic;
    private boolean isDamage = false;
    private TntFeature tntFeature;
    private SimplePointFeature pointFeature;

    public ListenerFeature(DefuseTheBomb expansion, SimpleGameArena arena, GameArenaLogic arenaLogic) {
        this.expansion = expansion;
        this.arena = arena;
        this.arenaLogic = arenaLogic;
    }

    public boolean isDamage() {
        return isDamage;
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, expansion.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public void init() {
        tntFeature = arena.getFeature(TntFeature.class);
        pointFeature = arena.getFeature(SimplePointFeature.class);
    }

    @Override
    public void postInit() {
        GameConfigFeature configFeature = arena.getFeature(GameConfigFeature.class);

        isDamage = Optional.ofNullable(configFeature.getString("tnt.damage"))
                .map(Boolean::parseBoolean)
                .orElse(isDamage);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damagee = event.getEntity();
        if (!(damagee instanceof Player)) return;
        Player player = (Player) damagee;

        Entity damager = event.getDamager();
        if (!(damager instanceof TNTPrimed)) return;
        if (!tntFeature.contains(damager))
            return;

        if (!this.isDamage)
            event.setDamage(0.0D);

        if (arenaLogic.isInGame()) {
            pointFeature.applyPoint(player.getUniqueId(), DefuseTheBomb.POINT_MINUS);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof TNTPrimed)) return;
        if (!tntFeature.contains(entity)) return;

        event.blockList().clear();
        if (!arenaLogic.isInGame()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (!arenaLogic.isInGame()) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof TNTPrimed)) return;
        TNTPrimed tnt = (TNTPrimed) entity;
        if (!tntFeature.contains(tnt)) return;

        Player player = event.getPlayer();
        pointFeature.applyPoint(player.getUniqueId(), DefuseTheBomb.POINT_PLUS);
        EntityUtil.despawnSafe(tnt);
    }
}
