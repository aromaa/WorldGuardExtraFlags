package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import java.util.List;

import org.bukkit.World;

public abstract class AbstractRegionContainerWrapper
{
	public abstract AbstractRegionQueryWrapper createQuery();
	public abstract AbstractRegionManagerWrapper get(World world);
	
	public abstract List<AbstractRegionManagerWrapper> getLoaded();
}
