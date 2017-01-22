package net.goldtreeservers.worldguardextraflags.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldedit.Location;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import net.elseland.xikage.MythicMobs.Mobs.EggManager;
import net.elseland.xikage.MythicMobs.Mobs.MythicMob;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.FlyFlag;
import net.goldtreeservers.worldguardextraflags.flags.GiveEffectsFlag;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event)
	{
		event.getPlayer().removeMetadata("WorldGuardExtraFlagsWaitingForTeleportationToBeDone", WorldGuardExtraFlagsPlugin.getPlugin());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeathEvent(PlayerDeathEvent event)
	{
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getEntity().getLocation());
		Boolean keepInventory = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getEntity()), WorldGuardExtraFlagsPlugin.keepInventory);
		if (keepInventory != null && keepInventory)
		{
			event.setKeepInventory(true);
			event.getDrops().clear();
		}
		
		Boolean keepExp = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getEntity()), WorldGuardExtraFlagsPlugin.keepExp);
		if (keepExp != null && keepExp)
		{
			event.setKeepLevel(true);
			event.setDroppedExp(0);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getPlayer().getLocation());
		String prefix = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.chatPrefix);
		String suffix = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.chatSuffix);
		
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
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getPlayer().getLocation());
		Location respawnLocation = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.respawnLocation);
		if (respawnLocation != null)
		{
			event.setRespawnLocation(BukkitUtil.toLocation(respawnLocation));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
	{
		ItemMeta itemMeta = event.getItem().getItemMeta();
		if (itemMeta instanceof PotionMeta)
		{
			WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().get(event.getPlayer()).getHandler(GiveEffectsFlag.class).drinkPotion(Potion.fromItemStack(event.getItem()).getEffects());
		}
		else
		{
			Material material = event.getItem().getType();
			if (material == Material.MILK_BUCKET)
			{
				WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().get(event.getPlayer()).getHandler(GiveEffectsFlag.class).drinkMilk();
				ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getPlayer().getLocation());
				
				List<PotionEffectType> effects = new ArrayList<PotionEffectType>();
				for(Set<PotionEffect> potionEffects : regions.queryAllValues(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.giveEffects))
				{
					if (potionEffects != null)
					{
						for(PotionEffect potionEffect : potionEffects)
						{
							if (potionEffect != null)
							{
								effects.add(potionEffect.getType());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					event.getPlayer().setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().get(event.getPlayer()).getHandler(FlyFlag.class).getFlyStatys());
				}
				catch(Exception ignored)
				{
					
				}
			}
		}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
	}

	@EventHandler(priority = EventPriority.LOWEST)	
	public void onPlayerInteractEvent(PlayerInteractEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isMythicMobsEnabled())
		{
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
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
									ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getAction() == Action.RIGHT_CLICK_BLOCK ? event.getClickedBlock().getLocation() : event.getPlayer().getLocation());
									State state = regions.queryValue(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.mythicMobsEggs);
									if (state == State.DENY)
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		if (WorldGuardExtraFlagsPlugin.isEssentialsEnabled()) //Essentials how dare u do this to me!?!
		{
			Player player = event.getPlayer();
			if (player.getGameMode() != GameMode.CREATIVE && !WorldGuardExtraFlagsPlugin.getEssentialsPlugin().getUser(player).isAuthorized("essentials.fly"))
			{
				//Essentials now turns off flight, fuck him
				
				try
				{
					player.setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().get(player).getHandler(FlyFlag.class).getFlyStatys());
				}
				catch(Exception ignored)
				{
					
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event)
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					event.getPlayer().setAllowFlight(WorldGuardExtraFlagsPlugin.getWorldGuard().getSessionManager().get(event.getPlayer()).getHandler(FlyFlag.class).getFlyStatys());
				}
				catch(Exception ignored)
				{
					
				}
			}
		}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event)
	{
		ApplicableRegionSet regions = WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionContainer().createQuery().getApplicableRegions(event.getPlayer().getLocation());
		State state = regions.queryState(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(event.getPlayer()), WorldGuardExtraFlagsPlugin.itemDurability);
		if (state == State.DENY)
		{
			event.setCancelled(true);
		}
	}
}
