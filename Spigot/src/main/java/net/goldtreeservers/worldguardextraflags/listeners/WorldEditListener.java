package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.session.SessionManager;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.handlers.WorldEditFlagHandler;

@RequiredArgsConstructor
public class WorldEditListener
{
	private final SessionManager sessionManager;
	
	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event)
	{
		Actor actor = event.getActor();
		if (actor instanceof Player player)
		{
			LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(((BukkitPlayer) player).getPlayer());
			if (this.sessionManager.hasBypass(localPlayer, event.getWorld()))
			{
				return;
			}

			event.setExtent(new WorldEditFlagHandler(event.getWorld(), event.getExtent(), player));
		}
	}
}
