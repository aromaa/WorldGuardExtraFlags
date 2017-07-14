package net.goldtreeservers.worldguardextraflags.flags.handlers;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import com.earth2me.essentials.User;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class GodmodeFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<GodmodeFlag>
    {
        @Override
        public GodmodeFlag create(Session session)
        {
            return new GodmodeFlag(session);
        }
    }
    
    @Getter private Boolean isGodmodeEnabled;
    private Boolean originalEssentialsGodmode;
	    
	protected GodmodeFlag(Session session)
	{
		super(session);
	}
	
	@Nullable
	@Override
    public State getInvincibility(Player player)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			State state = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation()).queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.GODMODE);
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
						this.originalEssentialsGodmode = user.isGodModeEnabled();
						
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
		else
		{
			return null;
		}
	}
}
