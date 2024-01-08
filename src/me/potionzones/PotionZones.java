package me.potionzones;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import me.potionzones.commands.CommandPotZones;
import me.potionzones.commands.PotZonesTabCompleter;
import me.potionzones.events.ZoneListener;
import me.potionzones.models.Zone;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;


public class PotionZones extends JavaPlugin{
	private FileConfiguration zonesConfig = null;
	private File zonesFile = null;
	private List<Zone> inMemoryZones = new ArrayList<Zone>();;
	
	PluginDescriptionFile pdffile = getDescription();
	public String version = pdffile.getVersion();
	public String nombre = ChatColor.YELLOW + "[" + ChatColor.BLUE + pdffile.getName() + ChatColor.YELLOW + "] ";
	public String rutaConfig, rutaZones;
	
	public ZoneListener zoneListener = null;
	
	public void onEnable() {
		commandHandler();
		registerZones();
		registerConfig();
		registerEvents();
		Bukkit.getConsoleSender().sendMessage(nombre + ChatColor.WHITE + " Ha sido activado (Version: " + ChatColor.RED + version + ChatColor.WHITE + " )");
	}
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage(nombre + ChatColor.WHITE + " Ha sido desactivado (Version: " + ChatColor.RED + version + ChatColor.WHITE + " )");
	}
	public void commandHandler() {
		this.getCommand("potzone").setExecutor(new CommandPotZones(this));
		this.getCommand("potzone").setTabCompleter(new PotZonesTabCompleter());
	}
	public void registerConfig() {
		File config = new File(this.getDataFolder(),"config.yml");
		rutaConfig = config.getPath();
		if(!config.exists())
		{
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
	}
	public void registerZones() {
		zonesFile = new File(this.getDataFolder(),"zones.yml");
		if(!zonesFile.exists())
		{
			this.getZonesConfig().options().copyDefaults(true);
			saveZonesToFile();
		}
		reloadZones();
		
	}
	
	public void registerEvents() {
		clearEvents();
		zoneListener = new ZoneListener(inMemoryZones, this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(zoneListener, this);
		
	}
	public void clearEvents() {
		if(zoneListener != null)
			HandlerList.unregisterAll(zoneListener);
	}
	
	public FileConfiguration getZonesConfig() {
		if(zonesConfig == null) {
			reloadZones();
		}
		return zonesConfig;
	}
	
	public List<Zone> getZones(){
		return inMemoryZones;
	}
	
	public void reloadZones() {
		if(zonesConfig == null) {
			zonesFile = new File(this.getDataFolder(),"zones.yml");
		}
		zonesConfig = YamlConfiguration.loadConfiguration(zonesFile);
		Reader defConfigStream;
		
		try {
			defConfigStream = new InputStreamReader(this.getResource("zones.yml"), "UTF8");
			if(defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				zonesConfig.setDefaults(defConfig);
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		inMemoryZones.clear();
	
		Map<String,Object> fileZones = zonesConfig.getConfigurationSection("Zones").getValues(false);
		for (Map.Entry<String,Object> entry : fileZones.entrySet()) {
			
			Location center = new Location(null, 0, 0, 0);
			Location start = new Location(null, 0, 0, 0);
			Location end = new Location(null, 0, 0, 0);
			String effectName = zonesConfig.getString("Zones." + entry.getKey() + ".effect.name");
			PotionEffectType effectType = PotionEffectType.getByName(effectName);
			if(effectType == null) {
				Bukkit.getConsoleSender().sendMessage(this.nombre + "No se encontró el efecto: " + effectName);
				continue;
			}
			int duration, amplifier, timeToApply;
			
			try {
				
				duration = Integer.parseInt(zonesConfig.getString("Zones." + entry.getKey() + ".effect.duration"));
				amplifier = Integer.parseInt(zonesConfig.getString("Zones." + entry.getKey() + ".effect.amplifier")) - 1;
				timeToApply = Integer.parseInt(zonesConfig.getString("Zones." + entry.getKey() + ".effect.timeToApply"));

				start.setX(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".startCoords.x")));
				start.setY(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".startCoords.y")));
				start.setZ(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".startCoords.z")));
				start.setWorld(this.getServer().getWorld(zonesConfig.getString("Zones." + entry.getKey() + ".world")));
				
				center.setX(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".centerCoords.x")));
				center.setY(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".centerCoords.y")));
				center.setZ(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".centerCoords.z")));
				center.setWorld(this.getServer().getWorld(zonesConfig.getString("Zones." + entry.getKey() + ".world")));
				
				end.setX(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".endCoords.x")));
				end.setY(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".endCoords.y")));
				end.setZ(Double.parseDouble(zonesConfig.getString("Zones." + entry.getKey() + ".endCoords.z")));
				end.setWorld(this.getServer().getWorld(zonesConfig.getString("Zones." + entry.getKey() + ".world")));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			Zone zone = new Zone(entry.getKey(), start, end, center, effectType, duration, amplifier, timeToApply);
			inMemoryZones.add(zone);
		}
	}
	
	public void saveZonesToFile() {
		try {
			zonesConfig.save(zonesFile);
		}catch(IOException e) {
			e.printStackTrace();
			
		}
	}
	
}
