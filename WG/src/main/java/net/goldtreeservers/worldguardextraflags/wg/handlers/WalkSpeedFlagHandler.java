package net.goldtreeservers.worldguardextraflags.wg.handlers;

import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class WalkSpeedFlagHandler extends AbstractSpeedFlagHandler
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
	public static class Factory extends HandlerWrapper.Factory<WalkSpeedFlagHandler>
	{
		public Factory(Plugin plugin)
		{
			super(plugin);
		}

		@Override
		public WalkSpeedFlagHandler create(Session session)
		{
			return new WalkSpeedFlagHandler(this.getPlugin(), session);
		}
	}
	
	protected WalkSpeedFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session, Flags.WALK_SPEED);
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
