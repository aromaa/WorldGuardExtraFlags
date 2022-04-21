package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.event.extent.EditSessionEvent;
import com.sk89q.worldedit.util.eventbus.EventHandler;
import com.sk89q.worldedit.util.eventbus.Subscribe;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.we.handlers.WorldEditFlagHandler;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class WorldEditListener
{
	private final WorldGuardPlugin worldGuardPlugin;
	private final RegionContainer regionContainer;
	private final SessionManager sessionManager;
	
	@Subscribe(priority = EventHandler.Priority.VERY_EARLY)
    public void onEditSessionEvent(EditSessionEvent event)
	{
		if (event.getActor() instanceof Player player)
		{
			LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(Bukkit.getPlayer(player.getUniqueId()));
			if (this.sessionManager.hasBypass(localPlayer, event.getWorld()))
			{
				return;
			}

			event.setExtent(new WorldEditFlagHandler(event.getWorld(), event.getExtent(), localPlayer, this.regionContainer));
		}
	}
}
