package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class GodmodeFlagHandler extends FlagValueChangeHandler<State>
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
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
		super(session, Flags.GODMODE);
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, State value)
	{
		this.handleValue(player, value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType)
	{
		this.handleValue(player, currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType)
	{
		this.handleValue(player, null);
		return true;
	}
	
	private void handleValue(LocalPlayer player, State state)
	{
		if (state != null)
		{
			this.isGodmodeEnabled = state == State.ALLOW;
		}
		else
		{
			this.isGodmodeEnabled = null;
		}
	}
	
	@Override
    public State getInvincibility(LocalPlayer player)
	{
		if (this.isGodmodeEnabled != null)
		{
			return this.isGodmodeEnabled ? State.ALLOW : State.DENY;
		}
		
		return null;
	}
}
