package net.goldtreeservers.worldguardextraflags.utils;

import org.bukkit.World;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class WorldUtils
{
	public static void doUnloadChunkFlagCheck(World world)
	{
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.getPlugin().getWorldGuardCommunicator().getRegionContainer().get(world).getRegions().values())
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == State.DENY)
			{
				WorldGuardExtraFlagsPlugin.getPlugin().getLogger().info("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");
				
				BlockVector min = region.getMinimumPoint();
				BlockVector max = region.getMaximumPoint();

				for(int x = min.getBlockX() << 16; x <= max.getBlockX() << 16; x++)
				{
					for(int z = max.getBlockZ() << 16; z <= max.getBlockZ() << 16; z++)
					{
						world.getChunkAt(x, z).load(true);
					}
				}
			}
		}
	}
}
