package net.goldtreeservers.worldguardextraflags.we;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WorldEditFlagHandler extends AbstractWorldEditFlagHandler
{
	public WorldEditFlagHandler(World world, Extent extent, Player player)
	{
		super(world, extent, player);
	}
	
	@Override
    public boolean setBlock(Vector location,  BlockState block) throws WorldEditException
    {
    	ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getPlugin().getWorldGuardCommunicator().getRegionContainer().get(this.world).getApplicableRegions(location);
    	
    	State state = WorldGuardUtils.queryState(this.player, this.world, regions.getRegions(), Flags.WORLDEDIT);
    	if (state != State.DENY)
    	{
    		return true;
    	}
    	
    	return false;
    }
}
