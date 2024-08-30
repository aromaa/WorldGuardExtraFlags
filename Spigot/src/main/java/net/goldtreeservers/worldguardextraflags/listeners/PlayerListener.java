package net.goldtreeservers.worldguardextraflags.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.session.Session;

import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.FlyFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class PlayerListener implements Listener
{
	private final WorldGuardExtraFlagsPlugin plugin;

	private final WorldGuardPlugin worldGuardPlugin;
	private final RegionContainer regionContainer;
	private final SessionManager sessionManager;
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		
		player.removeMetadata(WorldGuardUtils.PREVENT_TELEPORT_LOOP_META, this.plugin);
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event)
	{
		Player player = event.getEntity();

		LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);
		ApplicableRegionSet regions = this.regionContainer.createQuery().getApplicableRegions(localPlayer.getLocation());
		
		Boolean keepInventory = regions.queryValue(localPlayer, Flags.KEEP_INVENTORY);
		if (keepInventory != null)
		{
			event.setKeepInventory(keepInventory);
			
			if (keepInventory)
			{
				event.getDrops().clear();
			}
		}
		
		Boolean keepExp = regions.queryValue(localPlayer, Flags.KEEP_EXP);
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

		LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);
		ApplicableRegionSet regions = this.regionContainer.createQuery().getApplicableRegions(localPlayer.getLocation());
		
		String prefix = regions.queryValue(localPlayer, Flags.CHAT_PREFIX);
		if (prefix != null)
		{
			event.setFormat(prefix + event.getFormat());
		}

		String suffix = regions.queryValue(localPlayer, Flags.CHAT_SUFFIX);
		if (suffix != null)
		{
			event.setFormat(event.getFormat() + suffix);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);
		
		Location respawnLocation = this.regionContainer.createQuery().queryValue(localPlayer.getLocation(), localPlayer, Flags.RESPAWN_LOCATION);
		if (respawnLocation != null)
		{
			event.setRespawnLocation(BukkitAdapter.adapt(respawnLocation));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event)
	{
		Player player = event.getPlayer();
		
		ItemMeta itemMeta = event.getItem().getItemMeta();
		if (itemMeta instanceof PotionMeta potionMeta)
		{
			List<PotionEffect> effects = new ArrayList<>();
			if (potionMeta.getBasePotionType() != null)
			{
				effects.addAll(potionMeta.getBasePotionType().getPotionEffects());
			}

			effects.addAll(potionMeta.getCustomEffects());

			this.sessionManager.get(this.worldGuardPlugin.wrapPlayer(player)).getHandler(GiveEffectsFlagHandler.class).drinkPotion(player, effects);
		}
		else
		{
			Material material = event.getItem().getType();
			if (material == Material.MILK_BUCKET)
			{
				this.sessionManager.get(this.worldGuardPlugin.wrapPlayer(player)).getHandler(GiveEffectsFlagHandler.class).drinkMilk(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event)
	{
		Player player = event.getPlayer();
		
		Session wgSession = this.sessionManager.getIfPresent(this.worldGuardPlugin.wrapPlayer(player));
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
						PlayerListener.this.checkFlyStatus(player, player.getAllowFlight());
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
					PlayerListener.this.checkFlyStatus(player, null);
				}
			}.runTask(WorldGuardExtraFlagsPlugin.getPlugin());
		}
	}
	
	private void checkFlyStatus(Player player, Boolean originalValueOverwrite)
	{
		FlyFlagHandler flyFlagHandler = this.sessionManager.get(this.worldGuardPlugin.wrapPlayer(player)).getHandler(FlyFlagHandler.class);

		Boolean currentValue = flyFlagHandler.getCurrentValue();
		if (currentValue != null)
		{
			player.setAllowFlight(currentValue);
		}

		if (originalValueOverwrite != null)
		{
			flyFlagHandler.setOriginalFly(originalValueOverwrite);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemDamageEvent(PlayerItemDamageEvent event)
	{
		Player player = event.getPlayer();
		LocalPlayer localPlayer = this.worldGuardPlugin.wrapPlayer(player);

		if (this.regionContainer.createQuery().queryState(localPlayer.getLocation(), localPlayer, Flags.ITEM_DURABILITY) == State.DENY)
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerSpawnLocationEvent(PlayerSpawnLocationEvent event)
	{
		Player player = event.getPlayer();
		LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

		Location location = this.regionContainer.createQuery().queryValue(BukkitAdapter.adapt(event.getSpawnLocation()), localPlayer, Flags.JOIN_LOCATION);
		if (location != null)
		{
			event.setSpawnLocation(BukkitAdapter.adapt(location));
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		Boolean value = this.sessionManager.get(this.worldGuardPlugin.wrapPlayer(player)).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			player.setAllowFlight(value);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();

		//Some plugins toggle flight off on world change based on permissions,
		//so we need to make sure to force the flight status.
		Boolean value = this.sessionManager.get(this.worldGuardPlugin.wrapPlayer(player)).getHandler(FlyFlagHandler.class).getCurrentValue();
		if (value != null)
		{
			player.setAllowFlight(value);
		}
	}
}
