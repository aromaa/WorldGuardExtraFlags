package net.goldtreeservers.worldguardextraflags.wg.wrappers.v7;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionQueryWrapper;

@RequiredArgsConstructor
public class RegionQueryWrapper extends AbstractRegionQueryWrapper
{
	private final RegionQuery regionQuery;

	@Override
	public ApplicableRegionSet getApplicableRegions(Location location)
	{
		return this.regionQuery.getApplicableRegions(BukkitAdapter.adapt(location));
	}
}
