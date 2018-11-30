package net.goldtreeservers.worldguardextraflags.wg.wrappers.v6;

import org.bukkit.Bukkit;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WorldEditFlagHandler extends AbstractDelegateExtent
{
	protected final org.bukkit.World world;
	protected final org.bukkit.entity.Player player;
	
	protected WorldEditFlagHandler(World world, Extent extent, Player player)
	{
		super(extent);

		if (world instanceof BukkitWorld)
		{
			this.world = ((BukkitWorld)world).getWorld();
		}
		else
		{
			this.world = Bukkit.getWorld(world.getName());
		}
		
		this.player = Bukkit.getPlayer(player.getUniqueId());
	}

	@Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException
    {
    	ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionContainer().get(this.world).getApplicableRegions(location);
    	
    	State state = WorldGuardUtils.queryState(this.player, this.world, regions.getRegions(), Flags.WORLDEDIT);
    	if (state != State.DENY)
    	{
    		return super.setBlock(location, block);
    	}
    	
    	return false;
    }
}
