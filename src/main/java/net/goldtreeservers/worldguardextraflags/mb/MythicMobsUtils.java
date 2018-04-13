package net.goldtreeservers.worldguardextraflags.mb;

import org.bukkit.plugin.Plugin;

import lombok.Getter;
import net.elseland.xikage.MythicMobs.MythicMobs;

public class MythicMobsUtils
{
	@Getter private static MythicMobs mythicMobsPlugin;
	
	public static void onLoad(Plugin mythicMobsPlugin)
	{
		MythicMobsUtils.mythicMobsPlugin = (MythicMobs)mythicMobsPlugin;
	}
}
