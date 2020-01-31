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
import net.goldtreeservers.worldguardextraflags.flags.helpers.ForcedStateFlag.ForcedState;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class GlideFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<GlideFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public GlideFlagHandler create(Session session)
        {
            return new GlideFlagHandler(this.getPlugin(), session);
        }
    }
    
    private Boolean originalGlide;
    
	protected GlideFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
	}
	
	@Override
    public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		ForcedState state = WorldGuardUtils.queryValue(player, current.getWorld(), set.getRegions(), Flags.GLIDE);
		
		this.handleValue(player, state);
	}
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		ForcedState state = WorldGuardUtils.queryValue(player, to.getWorld(), toSet.getRegions(), Flags.GLIDE);
		
		this.handleValue(player, state);
		
		return true;
	}
	
	private void handleValue(Player player, ForcedState state)
	{
		if (state != null)
		{
			if (state == ForcedState.ALLOW)
			{
				return;
			}
			
			boolean value = (state == ForcedState.FORCE ? true : false);
			
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
}
