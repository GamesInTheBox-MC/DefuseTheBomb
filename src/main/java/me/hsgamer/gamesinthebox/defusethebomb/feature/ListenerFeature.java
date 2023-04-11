package me.hsgamer.gamesinthebox.defusethebomb.feature;

import me.hsgamer.gamesinthebox.defusethebomb.DefuseTheBomb;
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
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Optional;

public class ListenerFeature implements Feature, Listener {
    private final DefuseTheBomb expansion;
    private final SimpleGameArena arena;
    private boolean isDamage = false;

    public ListenerFeature(DefuseTheBomb expansion, SimpleGameArena arena) {
        this.expansion = expansion;
        this.arena = arena;
    }

    private TntFeature getTntFeature() {
        return this.arena.getFeature(TntFeature.class);
    }

    private SimplePointFeature getPointFeature() {
        return this.arena.getFeature(SimplePointFeature.class);
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, expansion.getPlugin());
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
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
        if (!this.getTntFeature().contains(damager))
            return;

        if (!this.isDamage)
            event.setDamage(0.0D);

        getPointFeature().applyPoint(player.getUniqueId(), DefuseTheBomb.POINT_MINUS);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof TNTPrimed)) return;
        TNTPrimed tnt = (TNTPrimed) entity;
        if (!this.getTntFeature().contains(tnt)) return;

        Player player = event.getPlayer();
        getPointFeature().applyPoint(player.getUniqueId(), DefuseTheBomb.POINT_PLUS);
        EntityUtil.despawnSafe(tnt);
    }
}
