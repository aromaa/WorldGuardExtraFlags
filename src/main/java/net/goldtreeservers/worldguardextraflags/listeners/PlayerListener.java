package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.elseland.xikage.MythicMobs.Mobs.EggManager;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.handlers.FlyFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.GiveEffectsFlag;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event)
	{
		event.getPlayer().removeMetadata("WGEFP-TPOEF", WorldGuardExtraFlagsPlugin.getPlugin());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(PlayerDeathEvent event)
	{
		Player player = event.getEntity();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Boolean keepInventory = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.KEEP_INVENTORY); //Not sure should we add bypass for this
		if (keepInventory != null && keepInventory)
		{
			event.setKeepInventory(true);
			event.getDrops().clear();
		}
		
		Boolean keepExp = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.KEEP_EXP); //Not sure should we add bypass for this
		if (keepExp != null && keepExp)
		{
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		String prefix = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.CHAT_PREFIX); //Not sure should we add bypass for this
		String suffix = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.CHAT_SUFFIX); //Not sure should we add bypass for this
		
		if (prefix != null)
		{
			event.setFormat(prefix + event.getFormat());
		}
		
		if (suffix != null)
		{
			event.setFormat(event.getFormat() + suffix);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Location respawnLocation = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.RESPAWN_LOCATION); //Not sure should we add bypass for this
		if (respawnLocation != null)
		{
			event.setRespawnLocation(BukkitUtil.toLocation(respawnLocation));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		
		if (!WorldGuardUtils.hasBypass(player))
		{
			ItemMeta itemMeta = event.getItem().getItemMeta();
			if (itemMeta instanceof PotionMeta)
			{
				WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(GiveEffectsFlag.class).drinkPotion(player, Potion.fromItemStack(event.getItem()).getEffects());
			}
			else
			{
				Material material = event.getItem().getType();
				if (material == Material.MILK_BUCKET)
				{
					WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(GiveEffectsFlag.class).drinkMilk(player);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event)
	{
		Player player = event.getPlayer();
		
		if (!WorldGuardUtils.hasBypass(player))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					try
					{
						event.getPlayer().setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlag.class).getCurrentValue());
					}
					catch(Exception ignored)
					{
						
					}
				}
			}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
		}
	}

	//Last time I checked, the plugin don't check for cancelled events
	@EventHandler(priority = EventPriority.LOWEST)	
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isMythicMobsEnabled())
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
										if (regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.MYTHICMOB_EGGS) == State.DENY)
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
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		
		if (WorldGuardExtraFlagsPlugin.isEssentialsEnable()) //Essentials how dare u do this to me!?!
		{
			if (!WorldGuardUtils.hasBypass(player))
			{
				if (player.getGameMode() != GameMode.CREATIVE && !WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player).isAuthorized("essentials.fly"))
				{
					//Essentials now turns off flight, fuck him
					
					try
					{
						player.setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlag.class).getCurrentValue());
					}
					catch(Exception ignored)
					{
						
					}
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		if (!WorldGuardUtils.hasBypass(player))
		{
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					try
					{
						event.getPlayer().setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getSessionManager().get(player).getHandler(FlyFlag.class).getCurrentValue());
					}
					catch(Exception ignored)
					{
						
					}
				}
			}.runTaskLater(WorldGuardExtraFlagsPlugin.getPlugin(), 2);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		if (regions.queryState(WorldGuardUtils.wrapPlayer(player), FlagUtils.ITEM_DURABILITY) == State.DENY) //Not sure should we add bypass for this
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event)
	{
		Player player = event.getPlayer();
		
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuardPlugin().getRegionContainer().createQuery().getApplicableRegions(player.getLocation());
		
		Location location = regions.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.JOIN_LOCATION); //Not sure should we add bypass for this
		if (location != null)
		{
			event.setSpawnLocation(BukkitUtil.toLocation(location));
		}
	}
}
