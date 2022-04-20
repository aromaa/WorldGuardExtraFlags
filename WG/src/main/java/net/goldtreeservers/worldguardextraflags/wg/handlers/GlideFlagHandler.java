package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.helpers.ForcedStateFlag.ForcedState;

public class GlideFlagHandler extends FlagValueChangeHandler<ForcedState>
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}

    public static class Factory extends Handler.Factory<GlideFlagHandler>
    {
		@Override
        public GlideFlagHandler create(Session session)
        {
            return new GlideFlagHandler(session);
        }
    }
    
    private Boolean originalGlide;
    
	protected GlideFlagHandler(Session session)
	{
		super(session, Flags.GLIDE);
	}

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, ForcedState value)
	{
		this.handleValue(player, player.getWorld(), value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, ForcedState currentValue, ForcedState lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, ForcedState lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(),null);
		return true;
	}

	private void handleValue(LocalPlayer player, World world, ForcedState state)
	{
		Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();

		if (!this.getSession().getManager().hasBypass(player, world) && state != null)
		{
			if (state == ForcedState.ALLOW)
			{
				return;
			}
			
			boolean value = (state == ForcedState.FORCE ? true : false);
			
			if (bukkitPlayer.isGliding() != value)
			{
				if (this.originalGlide == null)
				{
					this.originalGlide = bukkitPlayer.isGliding();
				}

				bukkitPlayer.setGliding(value);
			}
		}
		else
		{
			if (this.originalGlide != null)
			{
				bukkitPlayer.setGliding(this.originalGlide);
				
				this.originalGlide = null;
			}
		}
	}
}
