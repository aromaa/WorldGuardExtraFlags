package net.goldtreeservers.worldguardextraflags.flags;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public class WorldEditFlag extends AbstractDelegateExtent
{
	private final Actor actor;
	
	public WorldEditFlag(Extent extent, Actor actor)
	{
		super(extent);
		this.actor = actor;
	}
	
    @Override
    public boolean setBlock(Vector location, BaseBlock block) throws WorldEditException
    {
    	Player player = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPlayer(this.actor.getUniqueId());
    	if (WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().hasBypass(player, player.getWorld()))
    	{
    		return super.setBlock(location, block);
    	}
    	else
    	{
    		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(BukkitUtil.toLocation(player.getWorld(), location));
    		State state = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.worldEdit);
    		if (state != State.DENY)
    		{
    			return super.setBlock(location, block);
    		}
    		else
    		{
    			return false;
    		}
    	}
    }
}
