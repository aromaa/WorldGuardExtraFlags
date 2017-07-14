package net.goldtreeservers.worldguardextraflags.flags.handlers;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class TeleportOnEntryFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<TeleportOnEntryFlag>
    {
        @Override
        public TeleportOnEntryFlag create(Session session)
        {
            return new TeleportOnEntryFlag(session);
        }
    }
	    
	protected TeleportOnEntryFlag(Session session)
	{
		super(session);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			if (!player.hasMetadata("WGEFP-TPOEF"))
			{
				com.sk89q.worldedit.Location location = toSet.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.TELEPORT_ON_ENTRY);
				if (location != null)
				{
					player.setMetadata("WGEFP-TPOEF", new FixedMetadataValue(WorldGuardExtraFlagsPlugin.getPlugin(), null));
					player.teleport(BukkitUtil.toLocation(location));
				}
			}
		}
		
		return true;
	}
}
