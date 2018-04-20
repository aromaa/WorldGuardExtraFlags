package net.goldtreeservers.worldguardextraflags.listeners;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
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

import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.essentials.EssentialsUtils;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.FlyFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener
{
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
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Boolean keepInventory = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.KEEP_INVENTORY);
		if (Boolean.TRUE.equals(keepInventory))
		{
			event.setKeepInventory(true);
			event.getDrops().clear();
		}
		
		Boolean keepExp = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.KEEP_EXP);
		if (Boolean.TRUE.equals(keepExp))
		{
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
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
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Location respawnLocation =  WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.RESPAWN_LOCATION);
		if (respawnLocation != null)
		{
			event.setRespawnLocation(BukkitUtil.toLocation(respawnLocation));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		
		ItemMeta itemMeta = event.getItem().getItemMeta();
		if (itemMeta instanceof PotionMeta)
		{
			WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(GiveEffectsFlagHandler.class).drinkPotion(player, Potion.fromItemStack(event.getItem()).getEffects());
		}
		else
		{
			Material material = event.getItem().getType();
			if (material == Material.MILK_BUCKET)
			{
				WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(GiveEffectsFlagHandler.class).drinkMilk(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event)
	{
		Player player = event.getPlayer();
		
		Boolean value = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					Boolean value = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
					if (value != null)
					{
						player.setAllowFlight(value);
					}
				}
			}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
		}
	}

	//Re-enable if needed and the plugin checks for cancelled events
	/*@EventHandler(priority = EventPriority.LOWEST)	
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isMythicMobsPluginEnabled())
		{
			Player player = event.getPlayer();
			
			if (!WorldGuardUtils.hasBypass(player))
			{
				Action action = event.getAction();
				if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
				{
					if (event.hasItem())
					{
						ItemStack item = event.getItem();
						if (item.getType() == Material.MONSTER_EGG)
						{
							if (item.getItemMeta().hasLore())
							{
								List<String> lore = item.getItemMeta().getLore();
								if (lore.get(0).equals(ChatColor.DARK_GRAY + "" + ChatColor.ITALIC + "A Mythical Egg that can"))
								{
									MythicMob mm = EggManager.getMythicMobFromEgg(lore.get(2));
									if (mm != null)
									{
										ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(action == Action.RIGHT_CLICK_BLOCK ? event.getClickedBlock().getLocation() : player.getLocation());
										if (regions.queryValue(WorldGuardUtils.wrapPlayer(player), Flags.MYTHICMOB_EGGS) == State.DENY)
										{
											event.setCancelled(true);
											event.setUseItemInHand(Result.DENY);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}*/

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		
		if (WorldGuardExtraFlagsPlugin.isEssentialsPluginEnabled()) //Essentials how dare u do this to me!?!
		{
			if (player.getGameMode() != GameMode.CREATIVE && !EssentialsUtils.getPlugin().getUser(player).isAuthorized("essentials.fly"))
			{
				//Essentials now turns off flight, fuck him
				Boolean value = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
				if (value != null)
				{
					player.setAllowFlight(value);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		if (WorldGuardUtils.queryState(player, player.getWorld(), regions.getRegions(), Flags.ITEM_DURABILITY) == State.DENY)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Location location = WorldGuardUtils.queryValue(player, player.getWorld(), regions.getRegions(), Flags.JOIN_LOCATION);
		if (location != null)
		{
			event.setSpawnLocation(BukkitUtil.toLocation(location));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		Boolean value = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			player.setAllowFlight(value);
		}
	}
}
