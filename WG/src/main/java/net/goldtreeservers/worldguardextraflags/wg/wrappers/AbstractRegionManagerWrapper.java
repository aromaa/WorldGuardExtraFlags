package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
public abstract class AbstractRegionManagerWrapper
{
	protected final RegionManager regionManager;

	public ProtectedRegion getRegion(String id)
	{
    if(regionManager !=null) return regionManager.getRegion(id);
    else return null;
	}

	public abstract ApplicableRegionSet getApplicableRegions(Location location);

	public ApplicableRegionSet getApplicableRegions(ProtectedCuboidRegion protectedCuboidRegion)
	{
		if(regionManager !=null) return regionManager.getApplicableRegions(protectedCuboidRegion);
    else return null; //Will possibly have unintended consequences
	}

	public Map<String, ProtectedRegion> getRegions()
	{
    if(regionManager !=null) return regionManager.getRegions();
    else return null; //Will possibly have unintended consequences
  }
}
