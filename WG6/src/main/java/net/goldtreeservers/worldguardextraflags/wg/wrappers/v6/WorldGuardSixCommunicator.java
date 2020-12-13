package net.goldtreeservers.worldguardextraflags.wg.wrappers.v6;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractRegionContainerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.AbstractSessionManagerWrapper;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.WorldGuardCommunicator;

public class WorldGuardSixCommunicator implements WorldGuardCommunicator
{
	private AbstractSessionManagerWrapper sessionManager;
	private AbstractRegionContainerWrapper regionContainer;
	
	@Override
	public void onLoad(Plugin plugin) throws Exception
	{
		WorldGuardCommunicator.super.onLoad(plugin);
	}

	@Override
	public void onEnable(Plugin plugin) throws Exception
	{
		this.sessionManager = new SessionManagerWrapper(WorldGuardPlugin.inst().getSessionManager());
		this.regionContainer = new RegionContainerWrapper();
		
		WorldGuardCommunicator.super.onEnable(plugin);
	}

	@Override
	public boolean isLegacy()
	{
		return true;
	}

	@Override
	public FlagRegistry getFlagRegistry()
	{
		return WorldGuardPlugin.inst().getFlagRegistry();
	}

	@Override
	public AbstractSessionManagerWrapper getSessionManager()
	{
		return this.sessionManager;
	}

	@Override
	public AbstractRegionContainerWrapper getRegionContainer() 
	{
		return this.regionContainer;
	}

	@Override
	public LocalPlayer wrapPlayer(Player player)
	{
		return WorldGuardPlugin.inst().wrapPlayer(player);
	}

	@Override
	public <T> SetFlag<T> getCustomSetFlag(String name, Flag<T> flag)
	{
		return new CustomSetFlag<T>(name, flag);
	}

	@Override
	public AbstractDelegateExtent getWorldEditFlag(World world, Extent extent, com.sk89q.worldedit.entity.Player player)
	{
		return new WorldEditFlagHandler(world, extent, player);
	}

	@Override
	public void doUnloadChunkFlagCheck(org.bukkit.World world)
	{
		for (ProtectedRegion region : this.getRegionContainer().get(world).getRegions().values())
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == State.DENY)
			{
				System.out.println("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");
				
				BlockVector min = region.getMinimumPoint();
				BlockVector max = region.getMaximumPoint();

				for(int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; x++)
				{
					for(int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++)
					{
						world.getChunkAt(x, z).load(true);
					}
				}
			}
		}
	}

	@Override
	public boolean doUnloadChunkFlagCheck(org.bukkit.World world, Chunk chunk) 
	{
		for (ProtectedRegion region : this.getRegionContainer().get(world).getApplicableRegions(new ProtectedCuboidRegion("UnloadChunkFlagTester", new BlockVector(chunk.getX() * 16, 0, chunk.getZ() * 16), new BlockVector(chunk.getX() * 16 + 15, 256, chunk.getZ() * 16 + 15))))
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == State.DENY)
			{
				return false;
			}
		}
		
		return true;
	}
}
