package net.goldtreeservers.worldguardextraflags.utils;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public class WorldGuardUtils
{
	public static LocalPlayer wrapPlayer(Player player)
	{
		return WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().wrapPlayer(player);
	}
	
	public static boolean hasBypass(Player player)
	{
		return WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().hasBypass(player, player.getWorld());
		
		//TODO: Add flag & region specified permissions
	}
}
