package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.Chunk;
import org.bukkit.World;
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
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.utils.WorldUtils;

public class WorldListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldLoadEvent event)
	{
		WorldUtils.doUnloadChunkFlagCheck(event.getWorld());
	}

	@EventHandler(ignoreCancelled = true)
	public void onChunkUnloadEvent(ChunkUnloadEvent event)
	{
		World world = event.getWorld();
		Chunk chunk = event.getChunk();
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.getRegionManager(world).getApplicableRegions(new ProtectedCuboidRegion("UnloadChunkFlagTester", new BlockVector(chunk.getX() * 16, 0, chunk.getZ() * 16), new BlockVector(chunk.getX() * 16 + 15, 256, chunk.getZ() * 16 + 15))))
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == State.DENY)
			{
				event.setCancelled(true);
				break;
			}
		}
	}
}