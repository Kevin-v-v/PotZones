package me.potionzones.utilities;

import java.util.HashMap;
import java.util.Map;

public class Effects {
    public static Map<String, String> createPotionEffectsMap() {
        Map<String, String> potionEffectsMap = new HashMap<>();

        // Agregar las traducciones
        potionEffectsMap.put("SPEED", "Velocidad");
        potionEffectsMap.put("SLOW", "Lentitud");
        potionEffectsMap.put("INCREASE_DAMAGE", "Fuerza");
        potionEffectsMap.put("WEAKNESS", "Debilidad");
        potionEffectsMap.put("HEAL", "Cura Instantánea");
        potionEffectsMap.put("HARM", "Daño Instantáneo");
        potionEffectsMap.put("DAMAGE_RESISTANCE", "Resistencia");
        potionEffectsMap.put("JUMP", "Salto");
        potionEffectsMap.put("CONFUSION", "Náuseas");
        potionEffectsMap.put("REGENERATION", "Regeneración");
        potionEffectsMap.put("POISON", "Veneno");
        potionEffectsMap.put("NIGHT_VISION", "Visión nocturna");
        potionEffectsMap.put("HUNGER", "Hambre");
        potionEffectsMap.put("INVISIBILITY", "Invisibilidad");
        potionEffectsMap.put("BLINDNESS", "Ceguera");
        potionEffectsMap.put("CONFUSION", "Nausea");
        potionEffectsMap.put("FAST_DIGGING", "Prisa Minera");
        potionEffectsMap.put("FIRE_RESISTANCE", "Resistencia al Fuego");
        potionEffectsMap.put("ABSORPTION", "Absorción");
        potionEffectsMap.put("HEALTH_BOOST", "Salud Aumentada");
        potionEffectsMap.put("SATURATION", "Saturación");
        potionEffectsMap.put("SLOW_DIGGING", "Fatiga");
        potionEffectsMap.put("WATER_BREATHING", "Respiración");
        potionEffectsMap.put("WITHER", "Wither");
        
        return potionEffectsMap;
    }
}
