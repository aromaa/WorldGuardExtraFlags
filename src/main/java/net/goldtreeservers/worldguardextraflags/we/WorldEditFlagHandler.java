package net.goldtreeservers.worldguardextraflags.we;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class WorldEditFlagHandler extends AbstractDelegateExtent
{
	private final BukkitWorld world;
	private final Player player;
	
	public WorldEditFlagHandler(BukkitWorld world, Extent extent, Player player)
	{
		super(extent);

		this.world = world;
		this.player = player;
	}

	//TODO: Prebuild list
    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException
    {
    	ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(BukkitUtil.toLocation(this.world.getWorld(), location));
    	
    	State state = WorldGuardUtils.queryState(WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPlayer(this.player.getUniqueId()), this.world.getWorld(), regions.getRegions(), Flags.WORLDEDIT);
    	if (state != State.DENY)
    	{
    		return super.setBlock(location, block);
    	}
    	
    	return false;
    }
}
