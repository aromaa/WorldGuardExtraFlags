package net.goldtreeservers.worldguardextraflags.we;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public abstract class AbstractWorldEditFlagHandler extends AbstractDelegateExtent
{
	protected final org.bukkit.World world;
	protected final org.bukkit.entity.Player player;
	
	protected AbstractWorldEditFlagHandler(World world, Extent extent, Player player)
	{
		super(extent);

		this.world = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getWorld(world.getName());
		this.player = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPlayer(player.getUniqueId());
	}
	
    public abstract boolean setBlock(Vector location,  BlockState block) throws WorldEditException;

	//TODO: Prebuild list
	@Override
    public boolean setBlock(Vector location, @SuppressWarnings("rawtypes") BlockStateHolder block) throws WorldEditException
    {
    	if (this.setBlock(location, block.toImmutableState()))
    	{
    		return super.setBlock(location, block);
    	}
    	
    	return false;
    }
}
