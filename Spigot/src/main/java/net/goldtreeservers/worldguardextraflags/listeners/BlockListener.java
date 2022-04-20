package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.Set;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.RegionContainer;
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

import com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent;
import com.sk89q.worldguard.bukkit.event.block.PlaceBlockEvent;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

@RequiredArgsConstructor
public class BlockListener implements Listener
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;

	private final RegionContainer regionContainer;
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityBlockFormEvent(EntityBlockFormEvent event)
	{
		BlockState newState = event.getNewState();
		if (newState.getType() == Material.FROSTED_ICE)
		{
			ApplicableRegionSet regions = this.regionContainer.createQuery().getApplicableRegions(BukkitAdapter.adapt(newState.getLocation()));

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
