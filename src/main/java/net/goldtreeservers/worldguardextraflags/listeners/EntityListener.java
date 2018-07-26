package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class EntityListener implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void onPortalCreateEvent(PortalCreateEvent event)
	{
		for(Block block : event.getBlocks())
		{
			//Unable to get the player who created it....
			
			ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
			if (regions.queryValue(null, Flags.NETHER_PORTALS) == State.DENY)
			{
				event.setCancelled(true);
				break;
			}
		}
	}
}
