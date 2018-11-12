package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;

public abstract class AbstractRegionQueryWrapper
{
	public abstract ApplicableRegionSet getApplicableRegions(Location location);
}
