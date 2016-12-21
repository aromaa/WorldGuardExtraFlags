package net.goldtreeservers.worldguardextraflags.flags;

import javax.annotation.Nullable;

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

public class GodmodeFlag extends FlagValueChangeHandler<State>
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
    
    private Boolean isGodmodeEnabled;
    private Boolean originalEssentialsGodmode;
	    
	protected GodmodeFlag(Session session)
	{
		super(session, WorldGuardExtraFlagsPlugin.godmode);
	}
	
	@Nullable
	@Override
    public State getInvincibility(Player player)
	{
		if (!this.getSession().getManager().hasBypass(player, player.getWorld()))
		{
			ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
			return regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.godmode);
		}
		else
		{
			return null;
		}
	}
	
	public void updateGodmode(Player player, State newValue, World world)
	{
		this.isGodmodeEnabled = newValue == null ? null : newValue == State.ALLOW ? true : false;
		
		if (!this.getSession().getManager().hasBypass(player, world) && this.isGodmodeEnabled != null)
		{
			if (WorldGuardExtraFlagsPlugin.isEssentialsEnabled())
			{
				if (this.originalEssentialsGodmode == null)
				{
					this.originalEssentialsGodmode = WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player).isGodModeEnabledRaw();
				}
				
				WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player).setGodModeEnabled(this.isGodmodeEnabled);
			}
		}
		else
		{
			this.isGodmodeEnabled = null;
			
			if (this.originalEssentialsGodmode != null)
			{
				WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player).setGodModeEnabled(this.originalEssentialsGodmode);
				this.originalEssentialsGodmode = null;
			}
		}
	}

	@Override
	protected void onInitialValue(Player player, ApplicableRegionSet set, State value)
	{
		this.updateGodmode(player, value, player.getWorld());
	}

	@Override
	protected boolean onSetValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType)
	{
		this.updateGodmode(player, currentValue, player.getWorld());
		return true;
	}

	@Override
	protected boolean onAbsentValue(Player player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType)
	{
		this.updateGodmode(player, null, player.getWorld());
		return true;
	}
	
	public Boolean getIsGodmodEnbled()
	{
		return this.isGodmodeEnabled;
	}
}
