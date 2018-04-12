package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.earth2me.essentials.User;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class GodmodeFlagHandler extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<GodmodeFlagHandler>
    {
        @Override
        public GodmodeFlagHandler create(Session session)
        {
            return new GodmodeFlagHandler(session);
        }
    }
    
    @Getter private Boolean isGodmodeEnabled;
    private Boolean originalEssentialsGodmode;
	    
	protected GodmodeFlagHandler(Session session)
	{
		super(session);
	}
	
	@Override
	public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		State state = WorldGuardUtils.queryState(player, current.getWorld(), set.getRegions(), Flags.GODMODE);
		this.handleValue(player, state);
    }
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		State state = WorldGuardUtils.queryState(player, to.getWorld(), toSet.getRegions(), Flags.GODMODE);
		this.handleValue(player, state);
		
		return true;
	}
	
	private void handleValue(Player player, State state)
	{
		if (state != null)
		{
			this.isGodmodeEnabled = (state == State.ALLOW ? true : false);
		}
		else
		{
			this.isGodmodeEnabled = null;
		}
		
		if (WorldGuardExtraFlagsPlugin.isEssentialsEnable())
		{
			User user = WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player);
			
			if (this.isGodmodeEnabled != null)
			{
				if (this.isGodmodeEnabled != user.isGodModeEnabled())
				{
					if (this.originalEssentialsGodmode == null)
					{
						this.originalEssentialsGodmode = user.isGodModeEnabled();
					}
					
					user.setGodModeEnabled(this.isGodmodeEnabled);
				}
			}
			else
			{
				if (this.originalEssentialsGodmode != null)
				{
					user.setGodModeEnabled(this.originalEssentialsGodmode);
					
					this.originalEssentialsGodmode = null;
				}
			}
		}
	}
	
	@Nullable
	@Override
    public State getInvincibility(Player player)
	{
		if (this.isGodmodeEnabled != null)
		{
			return this.isGodmodeEnabled ? State.ALLOW : State.DENY;
		}
		
		return null;
	}
}
