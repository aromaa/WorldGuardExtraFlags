package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldguard.session.Session;

import com.sk89q.worldguard.session.handler.Handler;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

import org.bukkit.entity.Player;

public class FlySpeedFlagHandler extends AbstractSpeedFlagHandler
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
	public static class Factory extends Handler.Factory<FlySpeedFlagHandler>
	{
		@Override
		public FlySpeedFlagHandler create(Session session)
		{
			return new FlySpeedFlagHandler(session);
		}
	}
	
	protected FlySpeedFlagHandler(Session session)
	{
		super(session, Flags.FLY_SPEED);
	}
	
	@Override
	protected float getSpeed(Player player)
	{
		return player.getFlySpeed();
	}
	
	@Override
	protected void setSpeed(Player player, float speed)
	{
		player.setFlySpeed(speed);
	}
}
