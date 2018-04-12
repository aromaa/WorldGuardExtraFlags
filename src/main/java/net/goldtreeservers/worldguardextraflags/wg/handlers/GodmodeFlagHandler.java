package net.goldtreeservers.worldguardextraflags.wg.handlers;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.earth2me.essentials.User;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
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
	
	@Nullable
	@Override
    public State getInvincibility(Player player)
	{
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		State state = WorldGuardUtils.queryState(player, player.getWorld(), regions.getRegions(), Flags.GODMODE);
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
					user.setGodModeEnabled(this.isGodmodeEnabled);
					
					this.originalEssentialsGodmode = null;
				}
			}
		}
		
		return state;
	}
}
