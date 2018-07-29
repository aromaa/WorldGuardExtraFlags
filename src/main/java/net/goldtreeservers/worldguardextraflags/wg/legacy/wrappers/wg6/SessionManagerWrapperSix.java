package net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.wg6;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.SessionManager;
import com.sk89q.worldguard.session.handler.Handler;
import com.sk89q.worldguard.session.handler.Handler.Factory;

import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;

public class SessionManagerWrapperSix extends SessionManagerWrapper
{
	private Method getMethod;
	private Method getIfPresentMethod;
	
	private Method registerHandlerMethod;
	
	public SessionManagerWrapperSix(SessionManager sessionManager) throws NoSuchMethodException, SecurityException
	{
		super(sessionManager);
		
		this.getMethod = sessionManager.getClass().getMethod("get", Player.class);
		this.getIfPresentMethod = sessionManager.getClass().getMethod("getIfPresent", Player.class);
		
		this.registerHandlerMethod = sessionManager.getClass().getMethod("registerHandler", Factory.class, Factory.class);
	}

	@Override
	public Session get(Player player)
	{
		try
		{
			return (Session)this.getMethod.invoke(this.sessionManager, player);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Session getIfPresent(Player player)
	{
		try
		{
			return (Session)this.getIfPresentMethod.invoke(this.sessionManager, player);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void registerHandler(Factory<? extends Handler> factory)
	{
		try
		{
			this.registerHandlerMethod.invoke(this.sessionManager, factory, null);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}
}
