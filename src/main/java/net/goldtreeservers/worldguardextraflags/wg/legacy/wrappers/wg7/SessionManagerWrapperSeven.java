package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg7;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.session.handler.Handler.Factory;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;

public class SessionManagerWrapperSeven extends SessionManagerWrapper
{
	public SessionManagerWrapperSeven(SessionManager sessionManager)
	{
		super(sessionManager);
	}

	@Override
	public Session get(Player player)
	{
		return this.sessionManager.get(WorldGuardExtraFlagsPlugin.getPlugin().getWorldGuardCommunicator().wrapPlayer(player));
	}

	@Override
	public Session getIfPresent(Player player)
	{
		return this.sessionManager.getIfPresent(WorldGuardExtraFlagsPlugin.getPlugin().getWorldGuardCommunicator().wrapPlayer(player));
	}

	@Override
	public void registerHandler(Factory<? extends Handler> factory)
	{
		this.sessionManager.registerHandler(factory, null);
	}
}
