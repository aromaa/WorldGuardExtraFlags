package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WalkSpeedFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<WalkSpeedFlagHandler>
    {
        @Override
        public WalkSpeedFlagHandler create(Session session)
        {
            return new WalkSpeedFlagHandler(session);
        }
    }
    
    private Float originalWalkSpeed;
	    
	protected WalkSpeedFlagHandler(Session session)
	{
		super(session);
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
