package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg7;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.goldtreeservers.worldguardextraflags.wg.legacy.WorldGuardCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;

public class WorldGuardSevenCommunicator implements WorldGuardCommunicator
{
	private SessionManagerWrapper sessionManager;
	private RegionContainerWrapper regionContainer;
	
	public WorldGuardSevenCommunicator()
	{
	}

	@Override
	public void onLoad() throws Exception
	{
	}

	@Override
	public void onEnable() throws Exception
	{
		this.sessionManager = new SessionManagerWrapperSeven(WorldGuard.getInstance().getPlatform().getSessionManager());
		this.regionContainer = new RegionContainerWrapperSeven(WorldGuard.getInstance().getPlatform().getRegionContainer());
	}
	
	@Override
	public FlagRegistry getFlagRegistry()
	{
		return WorldGuard.getInstance().getFlagRegistry();
	}

	@Override
	public SessionManagerWrapper getSessionManager()
	{
		return this.sessionManager;
	}

	@Override
	public RegionContainerWrapper getRegionContainer()
	{
		return this.regionContainer;
	}
	@Override
	public LocalPlayer wrapPlayer(Player player)
	{
		return WorldGuardPlugin.inst().wrapPlayer(player);
	}
}
