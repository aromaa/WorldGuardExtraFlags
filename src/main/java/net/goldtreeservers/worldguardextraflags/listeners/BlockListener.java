package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.EntityBlockFormEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public class BlockListener implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event)
	{
		if (SupportedFeatures.isFrostwalkerSupported())
		{
			BlockState newState = event.getNewState();
			if (newState.getType() == Material.FROSTED_ICE)
			{
				ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(newState.getLocation()));
				
				Entity entity = event.getEntity();
				if (entity instanceof Player)
				{
					Player player = (Player)entity;
					if (WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.FROSTWALKER) == State.DENY)
					{
						event.setCancelled(true);
					}
				}
				else
				{
					if (regions.queryValue(null, Flags.FROSTWALKER) == State.DENY)
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	//TODO: Figure out something better for this
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockPlaceEvent(PlaceBlockEvent event)
	{
		Result originalResult = event.getResult();
		Object cause = event.getCause().getRootCause();

		if (cause instanceof Player)
		{
			Player player = (Player)cause;

			for(Block block : event.getBlocks())
			{
				ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));
				
				Set<Material> state = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.ALLOW_BLOCK_PLACE);
				if (state != null && state.contains(block.getType()))
				{
					event.setResult(Result.ALLOW);
				}
				else
				{
					Set<Material> state2 = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.DENY_BLOCK_PLACE);
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

	//TODO: Figure out something better for this
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BreakBlockEvent event)
	{
		Result originalResult = event.getResult();
		Object cause = event.getCause().getRootCause();
		
		if (cause instanceof Player)
		{
			Player player = (Player)cause;

			for(Block block : event.getBlocks())
			{
				ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(block.getLocation()));

				Set<Material> state = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.ALLOW_BLOCK_BREAK);
				if (state != null && state.contains(block.getType()))
				{
					event.setResult(Result.ALLOW);
				}
				else
				{
					Set<Material> state2 = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.DENY_BLOCK_BREAK);
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
