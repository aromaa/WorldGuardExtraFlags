package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg6;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.World;

import com.sk89q.worldguard.protection.managers.RegionManager;

import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionQueryWrapper;

public class RegionContainerWrapperSix extends RegionContainerWrapper
{
	protected final Object regionContainer;
	
	private Method createQueryMethod;
	private Method getMethod;
	
	public RegionContainerWrapperSix(Object regionContainer) throws NoSuchMethodException, SecurityException
	{
		this.regionContainer = regionContainer;
		
		this.createQueryMethod = regionContainer.getClass().getMethod("createQuery");
		this.getMethod = regionContainer.getClass().getMethod("get", World.class);
	}

	@Override
	public RegionQueryWrapper createQuery()
	{
		try
		{
			return new RegionQueryWrapperSix(this.createQueryMethod.invoke(this.regionContainer));
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public RegionManagerWrapper get(World world)
	{
		try
		{
			return new RegionManagerWrapperSix((RegionManager)this.getMethod.invoke(this.regionContainer, world));
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
