package net.goldtreeservers.worldguardextraflags.wg.wrappers.v6;

import org.bukkit.Location;

import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.protection.ApplicableRegionSet;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionQueryWrapper;

@RequiredArgsConstructor
public class RegionQueryWrapper extends AbstractRegionQueryWrapper
{
	private final RegionQuery regionQuery;
	
	@Override
	public ApplicableRegionSet getApplicableRegions(Location location)
	{
		return this.regionQuery.getApplicableRegions(location);
	}
}
