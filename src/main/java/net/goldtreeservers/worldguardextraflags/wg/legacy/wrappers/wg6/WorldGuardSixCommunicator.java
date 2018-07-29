package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg6;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;

import net.goldtreeservers.worldguardextraflags.wg.legacy.WorldGuardCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;

public class WorldGuardSixCommunicator implements WorldGuardCommunicator
{
	private FlagRegistry flagRegistry;
	private SessionManagerWrapper sessionManager;
	private RegionContainerWrapper regionContainer;
	
	public void onLoad() throws Exception
	{
		Method getFlagRegistryMethod = WorldGuardPlugin.class.getMethod("getFlagRegistry");
		
		this.flagRegistry = (FlagRegistry)getFlagRegistryMethod.invoke(WorldGuardPlugin.inst());
	}
	
	public void onEnable() throws Exception
	{
		Method getSessionManagerMethod = WorldGuardPlugin.class.getMethod("getSessionManager");
		Method getRegionContainerMethod = WorldGuardPlugin.class.getMethod("getRegionContainer");

		this.sessionManager = new SessionManagerWrapperSix((SessionManager)getSessionManagerMethod.invoke(WorldGuardPlugin.inst()));
		this.regionContainer = new RegionContainerWrapperSix(getRegionContainerMethod.invoke(WorldGuardPlugin.inst()));
	}
	
	@Override
	public FlagRegistry getFlagRegistry()
	{
		return this.flagRegistry;
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
