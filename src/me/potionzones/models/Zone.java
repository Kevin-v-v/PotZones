package me.potionzones.models;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.potionzones.PotionZones;
import me.potionzones.utilities.Effects;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.ChatColor;

public class Zone {
	
	private String name;

	private Location start;
    private Location end;
    private Location center;
    private PotionEffectType effectType;
    private int amplifier;
    private int duration;
    private int timeToApply;
    private Map<Player, Long> enterTimes;
    
    public Zone(String name,Location start, Location end, Location center, PotionEffectType effectType, int duration, int amplifier, int timeToApply) {
    	this.name = name;
    	this.start = start;
        this.end = end;
        this.center = center;
        this.effectType = effectType;
        this.duration = duration * 20;
        this.amplifier = amplifier;
        this.timeToApply = timeToApply * 1000;
        this.enterTimes = new HashMap<>();
    }

    public boolean hasCrossedRegionBoundaries(Location from, Location to) {
        return (isInsideRegion(from) != isInsideRegion(to));
    }

    public boolean isInsideRegion(Location location) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return (x >= start.getX() && x <= end.getX() &&
                y >= start.getY() && y <= end.getY() &&
                z >= start.getZ() && z <= end.getZ());
    }
    
    public boolean wasAlreadyInRegion(Player player) {
    	return enterTimes.containsKey(player);
    }

    public void onEnter(Player player, PotionZones plugin) {
        enterTimes.put(player, System.currentTimeMillis());
        startPeriodicCheck(player, plugin);
    }
    
    public void applyEffect(Player player) {
    	if(effectType == null) {
    		return;
    	}
    	PotionEffect effect = new PotionEffect(effectType, duration, amplifier);
    	player.addPotionEffect(effect, true);
    	

    }

    public void onExit(Player player) {

        // Verifica si el jugador ha entrado previamente a la región
        if (enterTimes.containsKey(player)) {
            enterTimes.remove(player); // Elimina el registro del jugador al salir
        }
    }
    private void startPeriodicCheck(Player player, PotionZones plugin) {
    	int updateTimeInZone;
    	try {
			updateTimeInZone = Integer.parseInt(plugin.getConfig().getString("Config.updateTimeInZone"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			updateTimeInZone = 10;
		}
    	
        new BukkitRunnable() {
            @Override
            public void run() {
                // Este método se ejecuta cada vez que se inicia el bucle programado

            	if(!plugin.getZonesConfig().contains("Zones." + name)) {
            		this.cancel();
            		return;
            	}
                if (isInsideRegion(player.getLocation()) && player.isOnline()) {
                	
                	Long timeInside = System.currentTimeMillis()-enterTimes.get(player);
                	
                	double progress = (double)(timeInside) / (double)(timeToApply) * 100.0	;
                	int progressProcessed= (int)Math.floor(progress) <= 100 ? (int)Math.floor(progress) : 100 ;
                	
                	//int progress = (int)Math.floor(timeInside/1000) < 10 ? (int)Math.floor(timeInside/1000) : 10;
            		String actionBarText = "{\"text\": \"&f&k&l||&r&l " + progressProcessed + "% ";

            		for(int i = 0; i < 20; i++) {
            			if(i < (int)(progress / 5))
            				actionBarText += "&a=";
            			else
            				
            				actionBarText += "&c-";
            		}
            		
            		actionBarText += "> &f&k&l||\"}";
            		actionBarText = ChatColor.translateAlternateColorCodes('&', actionBarText);
            		
            		CraftPlayer playerr = (CraftPlayer) player;
                    IChatBaseComponent chatBaseComponent = ChatSerializer.a(actionBarText);
                    PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatBaseComponent, (byte) 2);
                    playerr.getHandle().playerConnection.sendPacket(packetPlayOutChat);
                    
                	if(timeInside > timeToApply - 50) {
                		timeInside += 50;
                		
                		if(!player.hasPotionEffect(effectType))
                			player.playSound(player.getLocation(), Sound.CAT_MEOW, 100F, 1F);
                		
                		applyEffect(player);
                		
                		
                		
                    	String efecto = Effects.createPotionEffectsMap().get(effectType.getName());
                		String jsonTitle = "{" +
                                "\"text\": \"\"," + // Texto principal vacío
                                "\"extra\": [" +
                                "   {\"text\": \"" + "Efecto " + "\", \"bold\": true, \"color\": \"" + ChatColor.AQUA.name().toLowerCase() + "\"}," +
                                "   {\"text\": \"" + "Activado" + "\", \"bold\": true,\"color\": \"" + ChatColor.GREEN.name().toLowerCase() + "\"}" +
                                "]" +
                                "}";
                		String jsonSubTitle = "{" +
                                "\"text\": \"\"," + // Texto principal vacío
                                "\"extra\": [" +
                                "   {\"text\": \"" + efecto + " \", \"color\": \"" + ChatColor.WHITE.name().toLowerCase() + "\"}," +
                                "   {\"text\": \"" + (amplifier + 1) + " \", \"color\": \"" + ChatColor.RED.name().toLowerCase() + "\"}," +
                                "   {\"text\": \"(" + (duration / 20) + " segundos)\", \"color\": \"" + ChatColor.GRAY.name().toLowerCase() + "\"}" +
                                "]" +
                                "}";
                		

                		
                    	IChatBaseComponent chatTitle = ChatSerializer.a(jsonTitle);
                    	IChatBaseComponent chatSubTitle = ChatSerializer.a(jsonSubTitle);

                    	PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle);
                    	PacketPlayOutTitle length = new PacketPlayOutTitle(0, 100, 10);
                    	PacketPlayOutTitle subTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubTitle);

                    	((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
                    	((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);
                    	((CraftPlayer) player).getHandle().playerConnection.sendPacket(subTitle);
                    	((CraftPlayer) player).getHandle().playerConnection.sendPacket(length);

                    	
                	}	
                	
                } else {
                    // Si el jugador ya no está dentro de la región, puedes detener el bucle
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, updateTimeInZone);
    }

	public String getName() {
		return name;
	}
	public Location getStart() {
		return start;
	}

	public Location getEnd() {
		return end;
	}

	public Location getCenter() {
		return center;
	}

	public PotionEffectType getEffectType() {
		return effectType;
	}

	public int getAmplifier() {
		return amplifier;
	}

	public int getDuration() {
		return duration / 20;
	}

	public int getTimeToApply() {
		return timeToApply;
	}


}

