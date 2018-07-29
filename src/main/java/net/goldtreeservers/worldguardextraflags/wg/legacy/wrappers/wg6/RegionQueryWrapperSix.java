package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg6;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.ApplicableRegionSet;

import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionQueryWrapper;

public class RegionQueryWrapperSix extends RegionQueryWrapper
{
	protected final Object regionQuery;
	
	private Method getApplicableRegionsMethod;
	
	public RegionQueryWrapperSix(Object regionQuery) throws NoSuchMethodException, SecurityException
	{
		this.regionQuery = regionQuery;
		
		this.getApplicableRegionsMethod = regionQuery.getClass().getMethod("getApplicableRegions", Location.class);
	}

	@Override
	public ApplicableRegionSet getApplicableRegions(Location location)
	{
		try
		{
			return (ApplicableRegionSet)this.getApplicableRegionsMethod.invoke(this.regionQuery, location);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
