package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;

public abstract class RegionQueryWrapper
{
	public abstract ApplicableRegionSet getApplicableRegions(Location location);
}
