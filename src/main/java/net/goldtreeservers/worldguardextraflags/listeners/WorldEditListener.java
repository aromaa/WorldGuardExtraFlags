package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import net.goldtreeservers.worldguardextraflags.we.WorldEditFlagHandler;

public class WorldEditListener
{
	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event)
	{
		Actor actor = event.getActor();
		if (actor != null && actor.isPlayer())
		{
			event.setExtent(new WorldEditFlagHandler((BukkitWorld)event.getWorld(), event.getExtent(), (Player)actor));
		}
	}
}
