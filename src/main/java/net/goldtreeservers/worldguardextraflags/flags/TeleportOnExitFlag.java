package net.goldtreeservers.worldguardextraflags.flags;

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

public class TeleportOnExitFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<TeleportOnExitFlag>
    {
        @Override
        public TeleportOnExitFlag create(Session session)
        {
            return new TeleportOnExitFlag(session);
        }
    }
	   
	protected TeleportOnExitFlag(Session session)
	{
		super(session);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		if (!player.hasMetadata("WorldGuardExtraFlagsWaitingForTeleportationToBeDone"))
		{
			for(ProtectedRegion region : exited)
			{
				com.sk89q.worldedit.Location location = region.getFlag(WorldGuardExtraFlagsPlugin.teleportOnExit);
				if (location != null)
				{
					player.setMetadata("WorldGuardExtraFlagsWaitingForTeleportationToBeDone", new FixedMetadataValue(WorldGuardExtraFlagsPlugin.getPlugin(), null));
					player.teleport(BukkitUtil.toLocation(location));
					break;
				}
			}
		}
		return true;
	}
}
