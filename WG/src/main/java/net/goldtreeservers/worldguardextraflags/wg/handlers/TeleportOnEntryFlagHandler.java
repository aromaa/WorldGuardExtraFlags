package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.we.WorldEditUtils;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class TeleportOnEntryFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<TeleportOnEntryFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public TeleportOnEntryFlagHandler create(Session session)
        {
            return new TeleportOnEntryFlagHandler(this.getPlugin(), session);
        }
    }
	    
	protected TeleportOnEntryFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
	}
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		Object location = WorldGuardUtils.queryValueUnchecked(player, to.getWorld(), entered, Flags.TELEPORT_ON_ENTRY);
		if (location != null && WorldGuardUtils.hasNoTeleportLoop(this.getPlugin(), player, location))
		{
			player.teleport(WorldEditUtils.toLocation(location));
		}
		
		return true;
	}
}
