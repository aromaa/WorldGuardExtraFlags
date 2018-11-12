package net.goldtreeservers.worldguardextraflags.wg.wrappers.v7;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractSessionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.WorldGuardCommunicator;

public class WorldGuardSevenCommunicator implements WorldGuardCommunicator
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
		this.sessionManager = new SessionManagerWrapper(WorldGuard.getInstance().getPlatform().getSessionManager());
		this.regionContainer = new RegionContainerWrapper();
	}

	@Override
	public FlagRegistry getFlagRegistry()
	{
		return WorldGuard.getInstance().getFlagRegistry();
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
