package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

@RequiredArgsConstructor
public class WorldEditListener
{
	private final WorldGuardExtraFlagsPlugin plugin;
	
	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event)
	{
		Actor actor = event.getActor();
		if (actor != null && actor.isPlayer())
		{
			event.setExtent(this.plugin.getWorldGuardCommunicator().getWorldEditFlag(event.getWorld(), event.getExtent(), (Player)actor));
		}
	}
}
