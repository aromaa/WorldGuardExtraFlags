package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import java.util.Map;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRegionManagerWrapper
{
	protected final RegionManager regionManager;

	public ProtectedRegion getRegion(String id)
	{
		return this.regionManager.getRegion(id);
	}

	public abstract ApplicableRegionSet getApplicableRegions(Location location);

	public ApplicableRegionSet getApplicableRegions(ProtectedCuboidRegion protectedCuboidRegion)
	{
		return this.regionManager.getApplicableRegions(protectedCuboidRegion);
	}

	public Map<String, ProtectedRegion> getRegions()
	{
		return this.regionManager.getRegions();
	}
}
