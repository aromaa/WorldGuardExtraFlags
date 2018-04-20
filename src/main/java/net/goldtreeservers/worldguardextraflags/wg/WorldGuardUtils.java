package net.goldtreeservers.worldguardextraflags.wg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.FlagValueCalculator;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.util.NormativeOrders;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;

public class WorldGuardUtils
{
	public static final String PREVENT_TELEPORT_LOOP_META = "WGEFP: TLP";
	
	public static LocalPlayer wrapPlayer(Player player)
	{
		return WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().wrapPlayer(player);
	}
	
	public static boolean hasBypass(Player player, World world, ProtectedRegion region, Flag<?> flag)
	{
		//Permission system that supports wildcars is really helpful here :)
		if (player.hasPermission("worldguard.region.bypass." + world.getName() + "." + region.getId() + "." + flag.getName()))
		{
			return true;
		}
		
		return false;
	}

	public static State queryState(Player player, World world, Set<ProtectedRegion> regions, StateFlag flag)
	{
		return WorldGuardUtils.createFlagValueCalculator(player, world, regions, flag).queryState(WorldGuardUtils.wrapPlayer(player), flag);
	}
	
	public static <T> T queryValue(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag)
	{
		return WorldGuardUtils.createFlagValueCalculator(player, world, regions, flag).queryValue(WorldGuardUtils.wrapPlayer(player), flag);
	}
	
	public static <T> Collection<T> queryAllValues(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag)
	{
		return WorldGuardUtils.createFlagValueCalculator(player, world, regions, flag).queryAllValues(WorldGuardUtils.wrapPlayer(player), flag);
	}
	
	public static <T> FlagValueCalculator createFlagValueCalculator(Player player, World world, Set<ProtectedRegion> regions, Flag<T> flag)
	{
		List<ProtectedRegion> checkForRegions = new ArrayList<>();
		for(ProtectedRegion region : regions)
		{
			if (!WorldGuardUtils.hasBypass(player, world, region, flag))
			{
				checkForRegions.add(region);
			}
		}
		
		NormativeOrders.sort(checkForRegions);
		
		ProtectedRegion global = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionManager(world).getRegion(ProtectedRegion.GLOBAL_REGION);
		if (global != null) //Global region can be null
		{
			if (WorldGuardUtils.hasBypass(player, world, global, flag)) //Lets do like this for now to reduce dublicated code
			{
				global = null;
			}
		}
		
		return new FlagValueCalculator(checkForRegions, global);
	}
}
