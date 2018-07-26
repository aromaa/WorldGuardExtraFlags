package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.ess3.api.IUser;
import net.ess3.api.events.GodStatusChangeEvent;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GodmodeFlagHandler;

public class EssentialsListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGodStatusChangeEvent(GodStatusChangeEvent event)
	{
		IUser user = event.getAffected();
		Player player = user.getBase();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));
		
		State state = WorldGuardUtils.queryState(player, player.getWorld(), regions.getRegions(), Flags.GODMODE);
		if (state != null)
		{
			if (WorldGuardExtraFlagsPlugin.getSessionManager().get(WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().wrapPlayer(player)).getHandler(GodmodeFlagHandler.class).getIsGodmodeEnabled() != null)
			{
				event.setCancelled(true);
			}
		}
	}
}
