package net.goldtreeservers.worldguardextraflags.handlers;

import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Bukkit;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WorldEditFlagHandler extends AbstractDelegateExtent
{
	protected final World weWorld;
	
	protected final org.bukkit.World world;
	protected final LocalPlayer player;
	
	public WorldEditFlagHandler(World world, Extent extent, Player player)
	{
		super(extent);
		
		this.weWorld = world;

		if (world instanceof BukkitWorld)
		{
			this.world = ((BukkitWorld) world).getWorld();
		}
		else
		{
			this.world = Bukkit.getWorld(world.getName());
		}
		
		this.player = WorldGuardPlugin.inst().wrapPlayer(((BukkitPlayer) player).getPlayer());
	}

	@Override
    public boolean setBlock(BlockVector3 location, BlockStateHolder block) throws WorldEditException
    {
    	ApplicableRegionSet regions = WorldGuard.getInstance().getPlatform().getRegionContainer().get(this.weWorld).getApplicableRegions(location);

    	State state = regions.queryState(this.player, Flags.WORLDEDIT);
    	if (state != State.DENY)
    	{
    		return super.setBlock(location, block);
    	}
    	
    	return false;
    }
}
