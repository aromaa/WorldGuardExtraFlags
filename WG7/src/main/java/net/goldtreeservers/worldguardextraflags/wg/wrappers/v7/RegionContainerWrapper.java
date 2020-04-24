package net.goldtreeservers.worldguardextraflags.wg.wrappers.v7;

import java.util.List;
import java.util.stream.Collectors;

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
	
	@Override
	public List<AbstractRegionManagerWrapper> getLoaded()
	{
		return WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().stream().map((m) -> new RegionManagerWrapper(m)).collect(Collectors.toList());
	}
}
