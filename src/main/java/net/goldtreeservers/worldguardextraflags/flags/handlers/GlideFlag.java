package net.goldtreeservers.worldguardextraflags.flags.handlers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class GlideFlag extends FlagValueChangeHandler<State>
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<GlideFlag>
    {
        @Override
        public GlideFlag create(Session session)
        {
            return new GlideFlag(session);
        }
    }
    
    private Boolean originalGlide;
    private Boolean currentValue;
    
	protected GlideFlag(Session session)
	{
		super(session, FlagUtils.GLIDE);
	}

	private void updateGlide(Player player, State newValue, World world)
	{
		if (!WorldGuardUtils.hasBypass(player) && newValue != null)
		{
			boolean value = (newValue == State.ALLOW ? true : false);
			
			if (player.isGliding() != value)
			{
				if (this.originalGlide == null)
				{
					this.originalGlide = player.isGliding();
				}
				
				player.setGliding(value);
			}
		}
		else
		{
			if (this.originalGlide != null)
			{
				player.setGliding(this.originalGlide);
				
				this.originalGlide = null;
			}
		}
	}
	
	@Override
	protected void onInitialValue(Player player, ApplicableRegionSet set, State value)
	{
    	this.updateGlide(player, value, player.getWorld());
	}

	@Override
	protected boolean onSetValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType)
	{
    	this.updateGlide(player, currentValue, to.getWorld());
    	
		return true;
	}

	@Override
	protected boolean onAbsentValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType)
	{
    	this.updateGlide(player, null, player.getWorld());
    	
		return true;
	}
	
    @Override
    public boolean testMoveTo(Player player, Location from, Location to, ApplicableRegionSet toSet, MoveType moveType)
    {
    	if (this.currentValue != null && player.isGliding() != this.currentValue)
    	{
    		player.setGliding(this.currentValue);
    	}
    	
		return true;
    }
}
