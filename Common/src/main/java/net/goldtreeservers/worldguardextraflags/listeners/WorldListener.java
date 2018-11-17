package net.goldtreeservers.worldguardextraflags.listeners;

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
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldLoadEvent(WorldLoadEvent event)
	{
		World world = event.getWorld();
		
		this.plugin.getWorldGuardCommunicator().doUnloadChunkFlagCheck(world);
	}

	@EventHandler(ignoreCancelled = true)
	public void onChunkUnloadEvent(ChunkUnloadEvent event)
	{
		World world = event.getWorld();
		Chunk chunk = event.getChunk();
		
		if (!this.plugin.getWorldGuardCommunicator().doUnloadChunkFlagCheck(world, chunk))
		{
			event.setCancelled(true);
		}
	}
}