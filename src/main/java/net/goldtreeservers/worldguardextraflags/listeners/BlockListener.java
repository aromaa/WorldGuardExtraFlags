package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class BlockListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isSupportFrostwalker())
		{
			BlockState newState = event.getNewState();
			if (newState.getType() == Material.FROSTED_ICE)
			{
				if (event.getEntity() instanceof Player)
				{
					Player player = (Player)event.getEntity();

					if (!WorldGuardUtils.hasBypass(player))
					{
						ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(newState.getLocation());
						if (regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.FROSTWALKER) == State.DENY)
						{
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockPlaceEvent(PlaceBlockEvent event)
	{
		Result originalResult = event.getResult();
		Object cause = event.getCause().getRootCause();

		if (cause instanceof Player)
		{
			Player player = (Player)cause;

			if (!WorldGuardUtils.hasBypass(player))
			{
				for(Block block : event.getBlocks())
				{
					ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());
					
					Set<Material> state = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.ALLOW_BLOCK_PLACE);
					if (state != null && state.contains(block.getType()))
					{
						event.setResult(Result.ALLOW);
					}
					else
					{
						Set<Material> state2 = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.DENY_BLOCK_PLACE);
						if (state2 != null && state2.contains(block.getType()))
						{
							event.setResult(Result.DENY);
							return;
						}
						else
						{
							event.setResult(originalResult);
							return;
						}
					}
		    	}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BreakBlockEvent event)
	{
		Result originalResult = event.getResult();
		Object cause = event.getCause().getRootCause();
		
		if (cause instanceof Player)
		{
			Player player = (Player)cause;

			if (!WorldGuardUtils.hasBypass(player))
			{
				for(Block block : event.getBlocks())
				{
					ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());

					Set<Material> state = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.ALLOW_BLOCK_BREAK);
					if (state != null && state.contains(block.getType()))
					{
						event.setResult(Result.ALLOW);
					}
					else
					{
						Set<Material> state2 = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.DENY_BLOCK_BREAK);
						if (state2 != null && state2.contains(block.getType()))
						{
							event.setResult(Result.DENY);
							return;
						}
						else
						{
							event.setResult(originalResult);
							return;
						}
					}
				}
			}
		}
	}
}
