package net.goldtreeservers.worldguardextraflags.wg.legacy;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg6.WorldGuardSixCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg7.WorldGuardSevenCommunicator;

public interface WorldGuardCommunicator
{
	public void onLoad() throws Exception;
	public void onEnable() throws Exception;
	
	public FlagRegistry getFlagRegistry();
	public SessionManagerWrapper getSessionManager();
	
	public RegionContainerWrapper getRegionContainer();
	
	public LocalPlayer wrapPlayer(Player player);
	
	public static WorldGuardCommunicator create()
	{
		try
		{
			Class.forName("com.sk89q.worldguard.WorldGuard"); //Only exists in WG 7
			
			return new WorldGuardSevenCommunicator();
		}
		catch (Throwable ignored)
		{
			
		}
		
		try
		{
			Class<?> clazz = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
			if (clazz.getMethod("getFlagRegistry") != null)
			{
				return new WorldGuardSixCommunicator();
			}
		}
		catch (Throwable ignored)
		{
			ignored.printStackTrace();
		}
		
		return null;
	}
}
