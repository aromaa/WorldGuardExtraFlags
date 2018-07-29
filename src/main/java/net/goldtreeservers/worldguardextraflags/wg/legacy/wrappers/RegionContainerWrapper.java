package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers;

import org.bukkit.World;

public abstract class RegionContainerWrapper
{
	public abstract RegionQueryWrapper createQuery();
	public abstract RegionManagerWrapper get(World world);
}
