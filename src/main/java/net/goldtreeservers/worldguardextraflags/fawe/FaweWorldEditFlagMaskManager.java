package net.goldtreeservers.worldguardextraflags.fawe;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.regions.FaweMask;
import com.boydti.fawe.regions.FaweMaskManager;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;

public class FaweWorldEditFlagMaskManager extends FaweMaskManager<Player> 
{
	public FaweWorldEditFlagMaskManager()
	{
		super("WorldGuardExtraFlags");
	}
	
    public ProtectedRegion getRegion(Player player, Location loc)
    {
        final com.sk89q.worldguard.LocalPlayer localplayer = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().wrapPlayer(player);
        RegionManager manager = WorldGuardExtraFlagsPlugin.getRegionManager(player.getWorld());
        final ProtectedRegion global = manager.getRegion("__global__");
        if (global != null && !isDenied(localplayer, global))
        {
            return global;
        }
        
        final ApplicableRegionSet regions = manager.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()).toVector());
        for (final ProtectedRegion region : regions)
        {
            if (!isDenied(localplayer, region))
            {
                return region;
            }
        }
        return null;
    }

    public boolean isDenied(LocalPlayer localplayer, ProtectedRegion region)
    {
        return region.getFlag(Flags.WORLDEDIT) == State.DENY;
    }

	@Override
	public FaweMask getMask(FawePlayer<Player> fawePlayer)
	{
		final Player player = fawePlayer.parent;
		final Location location = player.getLocation();
        final ProtectedRegion myregion = this.getRegion(player, location);
        
        if (myregion != null)
        {
            final BlockVector pos1;
            final BlockVector pos2;
            if (myregion.getId().equals("__global__"))
            {
                pos1 = new BlockVector(Integer.MIN_VALUE, 0, Integer.MIN_VALUE);
                pos2 = new BlockVector(Integer.MAX_VALUE, 255, Integer.MAX_VALUE);
            }
            else
            {
                pos1 = new BlockVector(myregion.getMinimumPoint().getBlockX(), myregion.getMinimumPoint().getBlockY(), myregion.getMinimumPoint().getBlockZ());
                pos2 = new BlockVector(myregion.getMaximumPoint().getBlockX(), myregion.getMaximumPoint().getBlockY(), myregion.getMaximumPoint().getBlockZ());
            }
            
            return new FaweMask(pos1, pos2)
            {
                @Override
                public String getName()
                {
                    return myregion.getId();
                }
            };
        }
        else
        {
            return null;
        }
	}
}
