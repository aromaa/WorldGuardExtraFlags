package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg7;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionQueryWrapper;

@RequiredArgsConstructor
public class RegionQueryWrapperSeven extends RegionQueryWrapper
{
	protected final RegionQuery regionQuery;

	@Override
	public ApplicableRegionSet getApplicableRegions(Location location)
	{
		return this.regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));
	}
}
