package me.potionzones.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

public class PotZonesTabCompleter implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		
		if(args[0].isBlank() || args[0].isEmpty() || args.length < 2) {
			List<String> comandos = new ArrayList<String>();
			List<String> completions = new ArrayList<String>();
			comandos.add("help");
			comandos.add("add");
			comandos.add("remove");
			comandos.add("zoneinfo");
			comandos.add("reload");
			StringUtil.copyPartialMatches(args[0], comandos, completions);
			Collections.sort(completions);
			
			return completions;
		}
		
		if(args[0].equalsIgnoreCase("add")) {
			sender.sendMessage(ChatColor.RED + "/potzone add <nombre> <efecto> [potencia] [duración] [bloques del centro al borde] [tiempo para aplicar efecto]");
			if(args.length == 3) {
				
				List<String> effects = new ArrayList<String>();
				List<String> completions = new ArrayList<String>();
				PotionEffectType[] potionEffects = PotionEffectType.values();
				
				for(PotionEffectType potionEffect : potionEffects) {
					if(potionEffect == null) {
						continue;
					}
					effects.add(potionEffect.getName());
					
				}
				StringUtil.copyPartialMatches(args[2], effects, completions);
				Collections.sort(completions);
				
				return completions;
				
			} 
			return null;
		}else {
			return null;
		}
	}

}
