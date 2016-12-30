package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
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

public class BlockListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isSupportingFrostwalker())
		{
			if (event.getNewState().getType() == Material.FROSTED_ICE)
			{
				ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getNewState().getLocation());
				State state = null;
				if (event.getEntity() instanceof Player)
				{
					state = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(((Player)event.getEntity())), WorldGuardExtraFlagsPlugin.frostwalker);
				}
				else
				{
					state = regions.queryValue(null, WorldGuardExtraFlagsPlugin.frostwalker);
				}
				
				if (state == State.DENY)
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockPlaceEvent(PlaceBlockEvent event)
	{
		if (event.getResult() == Result.DEFAULT)
		{
			Object cause = event.getCause().getRootCause();
			
			if (cause instanceof Player)
			{
				Player player = (Player)cause;

				if (!WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().hasBypass(player, player.getWorld()))
				{
					for(Block block : event.getBlocks())
					{
						ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());
						
						Set<Material> state = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.allowBlockPlace);
						if (state != null && state.contains(block.getType()))
						{
							event.setResult(Result.ALLOW);
						}
						else
						{
							Set<Material> state2 = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.denyBlockPlace);
							if (state2 != null && state2.contains(block.getType()))
							{
								event.setResult(Result.DENY);
								return;
							}
							else
							{
								event.setResult(Result.DEFAULT);
								return;
							}
						}
			    	}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreakEvent(BreakBlockEvent event)
	{
		if (event.getResult() == Result.DEFAULT)
		{
			Object cause = event.getCause().getRootCause();
			
			if (cause instanceof Player)
			{
				Player player = (Player)cause;

				if (!WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().hasBypass(player, player.getWorld()))
				{
					for(Block block : event.getBlocks())
					{
						ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(block.getLocation());
	
						Set<Material> state = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.allowBlockBreak);
						if (state != null && state.contains(block.getType()))
						{
							event.setResult(Result.ALLOW);
						}
						else
						{
							Set<Material> state2 = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.denyBlockBreak);
							if (state2 != null && state2.contains(block.getType()))
							{
								event.setResult(Result.DENY);
								return;
							}
							else
							{
								event.setResult(Result.DEFAULT);
								return;
							}
						}
					}
				}
			}
		}
	}
}
