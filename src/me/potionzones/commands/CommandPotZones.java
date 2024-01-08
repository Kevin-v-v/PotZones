package me.potionzones.commands;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import me.potionzones.PotionZones;
import me.potionzones.models.Zone;
import me.potionzones.utilities.Effects;
import me.potionzones.utilities.Utilities;

public class CommandPotZones implements CommandExecutor{

	private PotionZones plugin;
	public CommandPotZones(PotionZones plugin) {
		this.plugin = plugin;
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage(plugin.nombre + ChatColor.WHITE + "No puedes ejecutar comandos desde la consola");
			return false;
		} else {
			
			Player jugador = (Player) sender;
			
			if(args.length > 0) {
				
				switch(args[0].toLowerCase()) {
					case "help":
						help(jugador);
						break;
					case "reload":
						plugin.reloadConfig();
						plugin.reloadZones();
						plugin.registerEvents();
						jugador.sendMessage(plugin.nombre + " Se ha recargado la configuración");
						break;
					case "add":
						String message = addZone(jugador, args);
						jugador.sendMessage(plugin.nombre + " " + message);
						break;
					case "remove":
						remove(jugador);
						break;
					case "zoneinfo":
						zoneInfo(jugador);
						break;
					default:
						help(jugador);
						break;
				}
				
			}else {
			
				jugador.sendMessage(plugin.nombre + " Usa /potzone help para obtener ayuda");
				
			}
			
			return true;
		}
	}
	
	private String addZone(Player jugador, String[] args) {
		
		if(args.length < 3) {
			return "Uso: /potzone add <nombre> <efecto> [potencia] [duración] [bloques del centro al borde] [tiempo para aplicar efecto]";
		}
		String effect = args[2].toUpperCase();
		
		PotionEffectType potionEffectFound = PotionEffectType.getByName(effect.toUpperCase());
		
		String zoneName = args[1];
		
		Zone zone = plugin.getZones().stream().filter(currentZone -> zoneName.equals(currentZone.getName())).findAny().orElse(null);
		
		if(potionEffectFound == null) {
			return "Efecto no válido. Uso: /potzone add <nombre> <efecto> [potencia] [duración] [bloques del centro al borde] [tiempo para aplicar efecto]";
		}else if(zone != null) {
			return "Ya existe una zona con ese nombre";
		}
		
		int amplifier = args.length < 4 ? 1 : Integer.parseInt(args[3]);
		
		int duration;
		try {
			duration = Integer.parseInt(plugin.getConfig().getString("Config.effectDuration"));
		} catch (Exception e) {
			e.printStackTrace();
			duration = 60;
		}
		
		if(args.length > 4) {
			try {
				duration = Integer.parseInt(args[4]);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		
		int blocksFromCenterToBorder;
		try {
			blocksFromCenterToBorder = Integer.parseInt(plugin.getConfig().getString("Config.blocksFromCenterToBorder"));
		} catch (Exception e) {
			e.printStackTrace();
			blocksFromCenterToBorder = 2;
		}
		
		
		if(args.length > 5) {
			try {
				blocksFromCenterToBorder = Integer.parseInt(args[5]);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		int timeToApply;
		
		try {
			timeToApply = Integer.parseInt(plugin.getConfig().getString("Config.timeToApply"));
		} catch (Exception e) {
			e.printStackTrace();
			timeToApply = 10;
		}
		
		if(args.length > 6) {
			try {
				timeToApply = Integer.parseInt(args[6]);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Location center = Utilities.roundCenterTo0_5(jugador.getLocation());
		Location start = Utilities.cubeStartLocation(center, blocksFromCenterToBorder);
		Location end = Utilities.cubeEndLocation(center, blocksFromCenterToBorder);
		
		FileConfiguration zonesConfig = plugin.getZonesConfig();
		
		zonesConfig.set("Zones." + zoneName + ".world", center.getWorld().getName());
		
		zonesConfig.set("Zones." + zoneName + ".centerCoords.x", center.getX());
		zonesConfig.set("Zones." + zoneName + ".centerCoords.y", center.getY());
		zonesConfig.set("Zones." + zoneName + ".centerCoords.z", center.getZ());
		
		zonesConfig.set("Zones." + zoneName + ".startCoords.x", start.getX());
		zonesConfig.set("Zones." + zoneName + ".startCoords.y", start.getY());
		zonesConfig.set("Zones." + zoneName + ".startCoords.z", start.getZ());
		
		zonesConfig.set("Zones." + zoneName + ".endCoords.x", end.getX());
		zonesConfig.set("Zones." + zoneName + ".endCoords.y", end.getY());
		zonesConfig.set("Zones." + zoneName + ".endCoords.z", end.getZ());
		
		zonesConfig.set("Zones." + zoneName + ".effect.name", effect);
		zonesConfig.set("Zones." + zoneName + ".effect.amplifier", amplifier);
		zonesConfig.set("Zones." + zoneName + ".effect.duration", duration);
		zonesConfig.set("Zones." + zoneName + ".effect.timeToApply", timeToApply);
		
		plugin.saveZonesToFile();
		plugin.reloadZones();
		plugin.registerEvents();
		
		return "Zona \"" + zoneName + "\"guardada con exito.";
		
	}
	
	public void zoneInfo(Player jugador) {
		Location location = jugador.getLocation();
		List<Zone> zones = plugin.getZones();
		Boolean inZone = false;
		for(Zone zone : zones) {
			if(zone.isInsideRegion(location)) {
				jugador.sendMessage("");
				jugador.sendMessage("-------------------------------------");
				jugador.sendMessage(plugin.nombre + "Información de la zona");
				jugador.sendMessage(plugin.nombre + "Nombre: " + zone.getName());
				jugador.sendMessage(plugin.nombre + "Centro: X=" + zone.getCenter().getX() + " Y=" + zone.getCenter().getY() + " Z=" + zone.getCenter().getZ() );
				jugador.sendMessage(plugin.nombre + "Inicio: X=" + zone.getStart().getX() + " Y=" + zone.getStart().getY() + " Z=" + zone.getStart().getZ() );
				jugador.sendMessage(plugin.nombre + "Fin: X=" + zone.getEnd().getX() + " Y=" + zone.getEnd().getY() + " Z=" + zone.getEnd().getZ() );
				jugador.sendMessage(plugin.nombre + "Efecto: " + Effects.createPotionEffectsMap().get(zone.getEffectType().getName()));
				jugador.sendMessage(plugin.nombre + "Duración: " +  zone.getDuration() + " segundos");
				jugador.sendMessage(plugin.nombre + "Potencia: " + zone.getAmplifier() + 1 );
				jugador.sendMessage(plugin.nombre + "Tiempo para aplicar efecto: " + zone.getTimeToApply() / 1000 + " segundos");
				jugador.sendMessage("-------------------------------------");
				inZone = true;
			}
		}
		if(!inZone) {
			jugador.sendMessage(plugin.nombre +" No estás en una zona");
		}
	}
	
	public void remove(Player jugador) {
		Location location = jugador.getLocation();
		List<Zone> zones = plugin.getZones();
		FileConfiguration zonesConfig = plugin.getZonesConfig();
		
		Boolean inZone = false;
		for(Zone zone : zones) {
			if(zone.isInsideRegion(location)) {
				String path = "Zones." + zone.getName();
				zonesConfig.set(path, null);
				jugador.sendMessage(plugin.nombre + "Zona \"" + zone.getName()+ "\" eliminada.");
				inZone = true;
			}
		}
		if(!inZone) {
			jugador.sendMessage(plugin.nombre +" No estás en una zona");
		}else {
			 // Crear un ScheduledExecutorService con un solo hilo
	        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	        // Ejecutar el código después de 2 segundos
	        scheduler.schedule(() -> {
	        	plugin.saveZonesToFile();
				plugin.reloadZones();
				plugin.registerEvents();
	        }, 2, TimeUnit.SECONDS);

	        // Cerrar el ScheduledExecutorService después de su uso
	        scheduler.shutdown();
			
		}
		return;
	}
	
	public void help(Player jugador) {
		jugador.sendMessage("");
		jugador.sendMessage("------------------------------------------------");
		jugador.sendMessage(plugin.nombre + "/potzone reload Recarga la configuración");
		jugador.sendMessage(plugin.nombre + "/potzone add <nombre> <efecto> [potencia] [duración] [bloques del centro al borde] [tiempo para aplicar efecto] Añade nueva zona");
		jugador.sendMessage(plugin.nombre + "/potzone remove Elimina la zona o zonas en las que estás parado");
		jugador.sendMessage(plugin.nombre + "/potzone zoneinfo Muestra información de la zona en la que estás parado");
		jugador.sendMessage("------------------------------------------------");
		
		return;
	}

}
