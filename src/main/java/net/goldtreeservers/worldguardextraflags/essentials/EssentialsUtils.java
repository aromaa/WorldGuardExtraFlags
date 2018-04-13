package net.goldtreeservers.worldguardextraflags.essentials;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;

public class EssentialsUtils
{
	@Getter private static Essentials plugin;
	
	public static void onLoad(Plugin essentialsPlugin)
	{
		EssentialsUtils.plugin = (Essentials)essentialsPlugin;
	}
	
	public static void onEnable()
	{
		WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPluginManager().registerEvents(new EssentialsListener(), WorldGuardExtraFlagsPlugin.getPlugin());
	}
}
