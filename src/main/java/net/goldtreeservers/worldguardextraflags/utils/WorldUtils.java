package net.goldtreeservers.worldguardextraflags.utils;

import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class WorldUtils
{
	public static void doUnloadChunkFlagCheck(World world)
	{
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.getRegionManager(world).getRegions().values())
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == State.DENY)
			{
				WorldGuardExtraFlagsPlugin.getPlugin().getLogger().info("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");
				
				Location min = BukkitAdapter.adapt(world, region.getMinimumPoint());
				Location max = BukkitAdapter.adapt(world, region.getMaximumPoint());

				for(int x = min.getChunk().getX(); x <= max.getChunk().getX(); x++)
				{
					for(int z = min.getChunk().getZ(); z <= max.getChunk().getZ(); z++)
					{
						world.getChunkAt(x, z).load(true);
					}
				}
			}
		}
	}
}
