package net.goldtreeservers.worldguardextraflags.wg.wrappers.v6;

import org.bukkit.World;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionQueryWrapper;

public class RegionContainerWrapper extends AbstractRegionContainerWrapper
{
	@Override
	public AbstractRegionQueryWrapper createQuery()
	{
		return new RegionQueryWrapper(WorldGuardPlugin.inst().getRegionContainer().createQuery());
	}

	@Override
	public AbstractRegionManagerWrapper get(World world)
	{
		return new RegionManagerWrapper(WorldGuardPlugin.inst().getRegionManager(world));
	}
}
