package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.Session;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.we.WorldEditUtils;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.FlyFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;

@RequiredArgsConstructor
public class PlayerListener implements Listener
{
	@Getter private final WorldGuardExtraFlagsPlugin plugin;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		
		player.removeMetadata(WorldGuardUtils.PREVENT_TELEPORT_LOOP_META, WorldGuardExtraFlagsPlugin.getPlugin());
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		
		ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Boolean keepInventory = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.KEEP_INVENTORY);
		if (keepInventory != null)
		{
			event.setKeepInventory(keepInventory);
			
			if (keepInventory)
			{
				event.getDrops().clear();
			}
		}
		
		Boolean keepExp = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.KEEP_EXP);
		if (keepExp != null)
		{
			event.setKeepLevel(keepExp);

			if (keepExp)
			{
				event.setDroppedExp(0);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		String prefix = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.CHAT_PREFIX);
		String suffix = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.CHAT_SUFFIX);

		if (prefix != null)
		{
			event.setFormat(prefix + event.getFormat());
		}
		
		if (suffix != null)
		{
			event.setFormat(event.getFormat() + suffix);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Object respawnLocation = WorldGuardUtils.queryValueUnchecked(player, player.getWorld(), regions.getRegions(), Flags.RESPAWN_LOCATION);
		if (respawnLocation != null)
		{
			event.setRespawnLocation(WorldEditUtils.toLocation(respawnLocation));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		
		ItemMeta itemMeta = event.getItem().getItemMeta();
		if (itemMeta instanceof PotionMeta)
		{
			this.plugin.getWorldGuardCommunicator().getSessionManager().get(player).getHandler(GiveEffectsFlagHandler.class).drinkPotion(player, Potion.fromItemStack(event.getItem()).getEffects());
		}
		else
		{
			Material material = event.getItem().getType();
			if (material == Material.MILK_BUCKET)
			{
				this.plugin.getWorldGuardCommunicator().getSessionManager().get(player).getHandler(GiveEffectsFlagHandler.class).drinkMilk(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event)
	{
		Player player = event.getPlayer();
		
		Session wgSession = this.plugin.getWorldGuardCommunicator().getSessionManager().getIfPresent(player);
		if (wgSession != null)
		{
			Boolean value = wgSession.getHandler(FlyFlagHandler.class).getCurrentValue();
			if (value != null)
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						PlayerListener.this.checkFlyStatus(player);
					}
				}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
			}
		}
		else
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					PlayerListener.this.checkFlyStatus(player);
				}
			}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
		}
	}
	
	private void checkFlyStatus(Player player)
	{
		Boolean value = this.plugin.getWorldGuardCommunicator().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			player.setAllowFlight(value);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		if (WorldGuardUtils.queryState(player, player.getWorld(), regions.getRegions(), Flags.ITEM_DURABILITY) == State.DENY)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = this.plugin.getWorldGuardCommunicator().getRegionContainer().createQuery().getApplicableRegions(event.getSpawnLocation());
		
		Object location = WorldGuardUtils.queryValueUnchecked(player, event.getSpawnLocation().getWorld(), regions.getRegions(), Flags.JOIN_LOCATION);
		if (location != null)
		{
			event.setSpawnLocation(WorldEditUtils.toLocation(location));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		Boolean value = this.plugin.getWorldGuardCommunicator().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			player.setAllowFlight(value);
		}
	}
}
