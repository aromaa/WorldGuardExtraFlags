package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

@RequiredArgsConstructor
public class WorldListener implements Listener
{
	private final WorldGuardExtraFlagsPlugin plugin;

	private final RegionContainer regionContainer;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldLoadEvent event)
	{
		World world = event.getWorld();
		
		this.plugin.doUnloadChunkFlagCheck(world);
	}

	@EventHandler(ignoreCancelled = true)
	public void onChunkUnloadEvent(ChunkUnloadEvent event)
	{
		World world = event.getWorld();
		Chunk chunk = event.getChunk();

		this.doUnloadChunkFlagCheck(world, chunk);
	}

	private void doUnloadChunkFlagCheck(org.bukkit.World world, Chunk chunk)
	{
		RegionManager regionManager = this.regionContainer.get(BukkitAdapter.adapt(world));
		if (regionManager == null)
		{
			return;
		}

		for (ProtectedRegion region : regionManager.getApplicableRegions(new ProtectedCuboidRegion("UnloadChunkFlagTester", BlockVector3.at(chunk.getX() * 16, world.getMinHeight(), chunk.getZ() * 16), BlockVector3.at(chunk.getX() * 16 + 15, world.getMaxHeight(), chunk.getZ() * 16 + 15))))
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == StateFlag.State.DENY)
			{
				chunk.addPluginChunkTicket(this.plugin);
			}
		}
	}
}