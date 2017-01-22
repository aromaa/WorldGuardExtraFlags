package net.goldtreeservers.worldguardextraflags.flags;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public class FlyFlag extends FlagValueChangeHandler<State>
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<FlyFlag>
    {
        @Override
        public FlyFlag create(Session session)
        {
            return new FlyFlag(session);
        }
    }
    
    private Boolean originalFly;
    private Boolean currentValue;
	    
	protected FlyFlag(Session session)
	{
		super(session, WorldGuardExtraFlagsPlugin.fly);
	}
	
	private void updateFly(Player player, State newValue, World world)
	{
		if (!this.getSession().getManager().hasBypass(player, world))
		{
			this.currentValue = newValue == null ? null : newValue == State.ALLOW ? true : false;
			
	        if (this.currentValue != null)
	        {
	        	if (player.getAllowFlight() != this.currentValue)
	        	{
	            	if (this.originalFly == null)
	            	{
	            		this.originalFly = player.getAllowFlight();
	            	}
	            	
	            	player.setAllowFlight(this.currentValue);
	        	}
	        }
	        else
	        {
	        	if (this.originalFly != null)
	        	{
	        		player.setAllowFlight(this.originalFly);
	        		this.originalFly = null;
	        	}
	        }
		}
	}
	
    @Override
    protected void onInitialValue(Player player, ApplicableRegionSet set, State value)
    {
    	this.updateFly(player, value, player.getWorld());
    }

    @Override
    protected boolean onSetValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType)
    {
    	this.updateFly(player, currentValue, to.getWorld());
        return true;
    }

    @Override
    protected boolean onAbsentValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType)
    {
    	this.updateFly(player, null, player.getWorld());
        return true;
    }
    
    @Override
    public boolean testMoveTo(Player player, Location from, Location to, ApplicableRegionSet toSet, MoveType moveType)
    {
    	if (this.currentValue != null && player.getAllowFlight() != this.currentValue)
    	{
    		player.setAllowFlight(this.currentValue);
    	}
    	
		return true;
    }
    
    public Boolean getFlyStatys()
    {
    	return this.currentValue;
    }
}
