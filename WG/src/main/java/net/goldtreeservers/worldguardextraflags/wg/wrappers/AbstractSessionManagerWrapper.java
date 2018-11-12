package net.goldtreeservers.worldguardextraflags.wg.wrappers;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractSessionManagerWrapper
{
	protected final SessionManager sessionManager;

	public abstract Session get(Player player);
	public abstract Session getIfPresent(Player player);
	
	public abstract void registerHandler(Handler.Factory<? extends Handler> factory);
}
