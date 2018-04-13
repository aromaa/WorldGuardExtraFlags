package net.goldtreeservers.worldguardextraflags.fawe;

import org.bukkit.plugin.Plugin;

import com.boydti.fawe.FaweAPI;

import lombok.Getter;

public class FAWEUtils
{
	@Getter private static Plugin plugin;
	
	public static void onLoad(Plugin plugin)
	{
		FAWEUtils.plugin = plugin;
	}
	
	public static void onEnable()
	{
		FaweAPI.addMaskManager(new FaweWorldEditFlagMaskManager());
	}
}
