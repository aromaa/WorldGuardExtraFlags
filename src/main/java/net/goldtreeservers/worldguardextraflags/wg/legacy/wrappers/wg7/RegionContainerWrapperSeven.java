package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg7;

import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionQueryWrapper;

@RequiredArgsConstructor
public class RegionContainerWrapperSeven extends RegionContainerWrapper
{
	protected final RegionContainer regionContainer;

	@Override
	public RegionQueryWrapper createQuery()
	{
		return new RegionQueryWrapperSeven(this.regionContainer.createQuery());
	}

	@Override
	public RegionManagerWrapper get(World world)
	{
		return new RegionManagerWrapperSeven(this.regionContainer.get(BukkitAdapter.adapt(world)));
	}
}
