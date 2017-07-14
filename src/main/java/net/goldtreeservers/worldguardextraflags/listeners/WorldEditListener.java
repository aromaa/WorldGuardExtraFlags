package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import net.goldtreeservers.worldguardextraflags.flags.handlers.WorldEditFlag;

public class WorldEditListener
{
	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event)
	{
		if (event.getActor() != null) //Not a player?
		{
			event.setExtent(new WorldEditFlag(event.getExtent(), event.getActor()));
		}
	}
}
