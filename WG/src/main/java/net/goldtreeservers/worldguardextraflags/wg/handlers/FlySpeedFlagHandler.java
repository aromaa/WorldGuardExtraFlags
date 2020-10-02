package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FlySpeedFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<FlySpeedFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public FlySpeedFlagHandler create(Session session)
        {
            return new FlySpeedFlagHandler(this.getPlugin(), session);
        }
    }
    
    private Float originalFlySpeed;
	    
	protected FlySpeedFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
	}
	
	@Override
    public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		Double speed = WorldGuardUtils.queryValue(player, current.getWorld(), set.getRegions(), Flags.FLY_SPEED);
		this.handleValue(player, speed);
	}
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Double speed = WorldGuardUtils.queryValue(player, to.getWorld(), toSet.getRegions(), Flags.FLY_SPEED);
		this.handleValue(player, speed);
		
		return true;
	}
	
	private void handleValue(Player player, Double speed)
	{
        if (speed != null)
        {
    		if (speed > 1.0)
    		{
    			speed = 1.0;
    		}
    		else if (speed < -1.0)
    		{
    			speed = -1.0;
    		}
    		
        	if (player.getFlySpeed() != speed.floatValue())
        	{
        		if (this.originalFlySpeed == null)
        		{
        			this.originalFlySpeed = player.getFlySpeed();
        		}
            	
        		player.setFlySpeed(speed.floatValue());
        	}
        }
        else
        {
        	if (this.originalFlySpeed != null)
        	{
        		player.setFlySpeed(this.originalFlySpeed);
        		
        		this.originalFlySpeed = null;
        	}
        }
	}
}
