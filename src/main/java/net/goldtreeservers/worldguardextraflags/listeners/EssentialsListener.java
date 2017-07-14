package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.ess3.api.events.GodStatusChangeEvent;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.handlers.GodmodeFlag;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class EssentialsListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGodStatusChangeEvent(GodStatusChangeEvent event)
	{
		if (!WorldGuardUtils.hasBypass(event.getController().getBase()))
		{
			if (WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(event.getController().getBase()).getHandler(GodmodeFlag.class).getIsGodmodeEnabled() != null)
			{
				event.setCancelled(true);
			}
		}
	}
}
