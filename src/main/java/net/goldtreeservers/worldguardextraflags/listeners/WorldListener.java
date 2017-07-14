package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;

public class WorldListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldLoadEvent event)
	{
		WorldGuardExtraFlagsPlugin.doUnloadChunkFlagWorldCheck(event.getWorld());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChunkUnloadEvent(ChunkUnloadEvent event)
	{
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionManager(event.getWorld()).getApplicableRegions(new ProtectedCuboidRegion("UnloadChunkFlagTester", new BlockVector(event.getChunk().getX() * 16, 0, event.getChunk().getZ() * 16), new BlockVector(event.getChunk().getX() * 16 + 15, 256, event.getChunk().getZ() * 16 + 15))))
		{
			if (region.getFlag(FlagUtils.CHUNK_UNLOAD) == State.DENY)
			{
				event.setCancelled(true);
				break;
			}
		}
	}
}