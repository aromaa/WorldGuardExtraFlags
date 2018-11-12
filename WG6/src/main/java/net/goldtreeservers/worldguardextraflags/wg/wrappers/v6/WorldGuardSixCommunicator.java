package net.goldtreeservers.worldguardextraflags.wg.wrappers.v6;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractSessionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.WorldGuardCommunicator;

public class WorldGuardSixCommunicator implements WorldGuardCommunicator
{
	private AbstractSessionManagerWrapper sessionManager;
	private AbstractRegionContainerWrapper regionContainer;
	
	@Override
	public void onLoad() throws Exception
	{
	}

	@Override
	public void onEnable() throws Exception
	{
		this.sessionManager = new SessionManagerWrapper(WorldGuardPlugin.inst().getSessionManager());
		this.regionContainer = new RegionContainerWrapper();
	}

	@Override
	public FlagRegistry getFlagRegistry()
	{
		return WorldGuardPlugin.inst().getFlagRegistry();
	}

	@Override
	public AbstractSessionManagerWrapper getSessionManager()
	{
		return this.sessionManager;
	}

	@Override
	public AbstractRegionContainerWrapper getRegionContainer() 
	{
		return this.regionContainer;
	}

	@Override
	public LocalPlayer wrapPlayer(Player player)
	{
		return WorldGuardPlugin.inst().wrapPlayer(player);
	}

	@Override
	public <T> SetFlag<T> getCustomSetFlag(String name, Flag<T> flag)
	{
		return new CustomSetFlag<T>(name, flag);
	}
}
