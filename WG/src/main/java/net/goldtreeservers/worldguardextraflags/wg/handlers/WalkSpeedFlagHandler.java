package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class WalkSpeedFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<WalkSpeedFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public WalkSpeedFlagHandler create(Session session)
        {
            return new WalkSpeedFlagHandler(this.getPlugin(), session);
        }
    }
    
    private Float originalWalkSpeed;
	    
	protected WalkSpeedFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
	}
	
	@Override
    public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		Double speed = WorldGuardUtils.queryValue(player, current.getWorld(), set.getRegions(), Flags.WALK_SPEED);
		this.handleValue(player, speed);
	}
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Double speed = WorldGuardUtils.queryValue(player, to.getWorld(), toSet.getRegions(), Flags.WALK_SPEED);
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
    		
        	if (player.getWalkSpeed() != speed.floatValue())
        	{
        		if (this.originalWalkSpeed == null)
        		{
        			this.originalWalkSpeed = player.getWalkSpeed();
        		}
            	
        		player.setWalkSpeed(speed.floatValue());
        	}
        }
        else
        {
        	if (this.originalWalkSpeed != null)
        	{
        		player.setWalkSpeed(this.originalWalkSpeed);
        		
        		this.originalWalkSpeed = null;
        	}
        }
	}
}
