package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import org.bukkit.entity.Player;

public abstract class AbstractSpeedFlagHandler extends FlagValueChangeHandler<Double>
{
	private Float originalSpeed;
	
	protected AbstractSpeedFlagHandler(Session session, DoubleFlag flag)
	{
		super(session, flag);
	}
	
	protected abstract float getSpeed(Player player);
	protected abstract void setSpeed(Player player, float speed);

	@Override
	protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, Double value)
	{
		this.handleValue(player, player.getWorld(), value);
	}

	@Override
	protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Double currentValue, Double lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), currentValue);
		return true;
	}

	@Override
	protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, Double lastValue, MoveType moveType)
	{
		this.handleValue(player, (World) to.getExtent(), null);
		return true;
	}

	private void handleValue(LocalPlayer player, World world, Double speed)
	{
		Player bukkitPlayer = ((BukkitPlayer) player).getPlayer();

		if (!this.getSession().getManager().hasBypass(player, world) && speed != null)
		{
			if (speed > 1.0)
			{
				speed = 1.0;
			}
			else if (speed < -1.0)
			{
				speed = -1.0;
			}
			
			if (this.getSpeed(bukkitPlayer) != speed.floatValue())
			{
				if (this.originalSpeed == null)
				{
					this.originalSpeed = this.getSpeed(bukkitPlayer);
				}
				
				this.setSpeed(bukkitPlayer, speed.floatValue());
			}
		}
		else
		{
			if (this.originalSpeed != null)
			{
				this.setSpeed(bukkitPlayer, this.originalSpeed);
				
				this.originalSpeed = null;
			}
		}
	}
}
