package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldguard.session.Session;

import com.sk89q.worldguard.session.handler.Handler;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

import org.bukkit.entity.Player;

public class WalkSpeedFlagHandler extends AbstractSpeedFlagHandler
{
	public static final Factory FACTORY()
	{
		return new Factory();
	}
	
	public static class Factory extends Handler.Factory<WalkSpeedFlagHandler>
	{
		@Override
		public WalkSpeedFlagHandler create(Session session)
		{
			return new WalkSpeedFlagHandler(session);
		}
	}
	
	protected WalkSpeedFlagHandler(Session session)
	{
		super(session, Flags.WALK_SPEED);
	}
	
	@Override
	protected float getSpeed(Player player)
	{
		return player.getWalkSpeed();
	}
	
	@Override
	protected void setSpeed(Player player, float speed)
	{
		player.setWalkSpeed(speed);
	}
}
