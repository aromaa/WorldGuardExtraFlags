package net.goldtreeservers.worldguardextraflags.wg.wrappers.v7;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;

import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionQueryWrapper;

public class RegionContainerWrapper extends AbstractRegionContainerWrapper
{

	@Override
	public AbstractRegionQueryWrapper createQuery()
	{
		return new RegionQueryWrapper(WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery());
	}

	@Override
	public AbstractRegionManagerWrapper get(World world)
	{
		return new RegionManagerWrapper(WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)));
	}
}
