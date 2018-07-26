package net.goldtreeservers.worldguardextraflags.we;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WorldEditFlagHandler extends AbstractDelegateExtent
{
	private final org.bukkit.World world;
	private final org.bukkit.entity.Player player;
	
	public WorldEditFlagHandler(World world, Extent extent, Player player)
	{
		super(extent);

		this.world = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getWorld(world.getName());
		this.player = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPlayer(player.getUniqueId());
	}

	//TODO: Prebuild list
    @Override
    public boolean setBlock(Vector location, BlockStateHolder block) throws WorldEditException
    {
    	ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(BukkitAdapter.adapt(this.world, location)));
    	
    	State state = WorldGuardUtils.queryState(this.player, this.world, regions.getRegions(), Flags.WORLDEDIT);
    	if (state != State.DENY)
    	{
    		return super.setBlock(location, block);
    	}
    	
    	return false;
    }
}
