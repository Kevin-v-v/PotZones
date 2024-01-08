package me.potionzones.events;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.potionzones.PotionZones;
import me.potionzones.models.Zone;

import java.util.List;

public class ZoneListener implements Listener {

    private List<Zone> zones;
    private PotionZones plugin;

    public ZoneListener(List<Zone> zones, PotionZones plugin) {
    	this.zones = zones;
    	this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();
        

        // Verifica si el jugador ha cruzado los límites de alguna región
        for (Zone zone : zones) {
        	
            if (zone.hasCrossedRegionBoundaries(from, to)) {
                // Verifica si el jugador ha entrado o salido de la región
                if (zone.isInsideRegion(to)) {
                	zone.onEnter(player, plugin);
                } else {
                    zone.onExit(player);
                }
            }
            
        }
    }
}
