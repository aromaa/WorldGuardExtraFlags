package net.goldtreeservers.worldguardextraflags.wg.wrappers.v7;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;

import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionManagerWrapper;

public class RegionManagerWrapper extends AbstractRegionManagerWrapper
{
	public RegionManagerWrapper(RegionManager regionManager)
	{
		super(regionManager);
	}

	@Override
	public ApplicableRegionSet getApplicableRegions(Location location)
	{
		return this.regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
	}
}
