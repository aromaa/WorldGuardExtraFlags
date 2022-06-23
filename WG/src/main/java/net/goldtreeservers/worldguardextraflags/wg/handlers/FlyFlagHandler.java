package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class FlyFlagHandler extends FlagValueChangeHandler<State>
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
    public static class Factory extends Handler.Factory<FlyFlagHandler>
    {
		@Override
        public FlyFlagHandler create(Session session)
        {
            return new FlyFlagHandler(session);
        }
    }

    @Getter private Boolean currentValue;
    private Boolean originalFly;
	    
	protected FlyFlagHandler(Session session)
	{
		super(session, Flags.FLY);
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, State value)
	{
		this.handleValue(player, player.getWorld(), value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State currentValue, State lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, State lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), null);
		return true;
	}
	
	private void handleValue(LocalPlayer player, World world, State state)
	{
		Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();

		if (!this.getSession().getManager().hasBypass(player, world) && state != null)
		{
			boolean value = state == State.ALLOW;
			
			if (bukkitPlayer.getAllowFlight() != value)
			{
				if (this.originalFly == null)
				{
					this.originalFly = bukkitPlayer.getAllowFlight();
				}

				bukkitPlayer.setAllowFlight(value);
			}

			this.currentValue = value;
		}
		else
		{
			if (this.originalFly != null)
			{
				bukkitPlayer.setAllowFlight(this.originalFly);
				
				this.originalFly = null;
			}

			this.currentValue = null;
		}
	}
}
