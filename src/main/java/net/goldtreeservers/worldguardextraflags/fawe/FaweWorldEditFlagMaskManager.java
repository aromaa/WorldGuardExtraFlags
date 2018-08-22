package net.goldtreeservers.worldguardextraflags.fawe;

import com.boydti.fawe.regions.general.RegionFilter;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldguard.protection.flags.Flags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import com.boydti.fawe.object.FawePlayer;
import com.boydti.fawe.regions.FaweMask;
import com.boydti.fawe.regions.FaweMaskManager;
import com.sk89q.worldguard.LocalPlayer;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.RegionManagerWrapper;

public class FaweWorldEditFlagMaskManager extends FaweMaskManager<Player>
{
	private final WorldGuardExtraFlagsPlugin plugin;
	
	FaweWorldEditFlagMaskManager(WorldGuardExtraFlagsPlugin plugin)
	{
		super(plugin.getName());
		this.plugin = plugin;
	}

	@Override
	public RegionFilter getFilter(String world)
	{
		return new WorldGuardFilter(plugin, Bukkit.getWorld(world));
	}

	@Override
	public FaweMask getMask(FawePlayer<Player> fp, MaskType type)
	{
		final Player player = fp.parent;
		final LocalPlayer localplayer = this.plugin.getWorldGuardCommunicator().wrapPlayer(player);
		RegionManagerWrapper manager = this.plugin.getWorldGuardCommunicator().getRegionContainer().get(player.getWorld());

		return new FaweMask(new MultiRegion(manager, localplayer), null)
		{
			@Override
			public boolean isValid(FawePlayer player, MaskType type)
			{
				// We rely on the region mask instead of this
				return true;
			}
		};
	}

	/***
	 * ManagerRegion wraps a RegionManager and will provide results based upon the regions enclosed
	 */
	private static class MultiRegion extends AbstractRegion
	{
		private final RegionManagerWrapper manager;
		private final LocalPlayer localplayer;

		MultiRegion(RegionManagerWrapper manager, LocalPlayer localplayer)
		{
			super(null);
			this.manager = manager;
			this.localplayer = localplayer;
		}

		@Override
		public Vector getMinimumPoint()
		{
			return manager.getRegions().entrySet().stream()
					.map(s -> s.getValue().getMinimumPoint())
					.min(Vector::compareTo)
					.orElse(new BlockVector(Integer.MIN_VALUE, 0, Integer.MIN_VALUE));
		}

		@Override
		public Vector getMaximumPoint()
		{
			return manager.getRegions().entrySet().stream()
					.map(s -> s.getValue().getMaximumPoint())
					.min(Vector::compareTo)
					.orElse(new BlockVector(Integer.MAX_VALUE, 0, Integer.MAX_VALUE));
		}

		@Override
		public void expand(Vector... changes) {
			throw new UnsupportedOperationException("Region is immutable");
		}

		@Override
		public void contract(Vector... changes) {
			throw new UnsupportedOperationException("Region is immutable");
		}

		@Override
		public boolean contains(Vector position)
		{
			// Make sure that all these flags are not denied. Denies override allows.
			return  manager.getApplicableRegions(position).testState(
					localplayer,
					Flags.BUILD,
					Flags.BLOCK_PLACE,
					Flags.BLOCK_BREAK,
					net.goldtreeservers.worldguardextraflags.flags.Flags.WORLDEDIT
			);
		}

	}

}
