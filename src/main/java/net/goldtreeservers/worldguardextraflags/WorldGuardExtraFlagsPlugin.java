package net.goldtreeservers.worldguardextraflags;

import java.awt.Color;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.flags.FlyFlag;
import net.goldtreeservers.worldguardextraflags.flags.GlideFlag;
import net.goldtreeservers.worldguardextraflags.flags.GodmodeFlag;
import net.goldtreeservers.worldguardextraflags.flags.WalkSpeedFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.BlockedEffectsFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.CommandOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.CommandOnExitFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.ConsoleCommandOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.ConsoleCommandOnExitFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.GiveEffectsFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.PlaySoundsFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.TeleportOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.handlers.TeleportOnExitFlag;
import net.goldtreeservers.worldguardextraflags.listeners.BlockListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListenerOnePointNine;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;
import net.goldtreeservers.worldguardextraflags.listeners.PlayerListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldEditListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldListener;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.PluginUtils;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	@Getter private static WorldGuardExtraFlagsPlugin plugin;
	@Getter private static WorldGuardPlugin worldGuardPlugin;
	@Getter private static WorldEditPlugin worldEditPlugin;
	@Getter private static Essentials essentialsPlugin;

	@Getter private static boolean supportsMobEffectColors;
	@Getter private static boolean supportFrostwalker;
	@Getter private static boolean supportsStopSound;
	@Getter private static boolean mythicMobsEnabled;
	@Getter private static boolean fastAsyncWorldEditEnabled;
	
	public WorldGuardExtraFlagsPlugin()
	{
		WorldGuardExtraFlagsPlugin.plugin = this;
		
		try
		{
			if (Material.FROSTED_ICE != null) //LOL, Just making it look nice xD
			{
				WorldGuardExtraFlagsPlugin.supportFrostwalker = true;
			}
		}
		catch (Throwable ignored)
		{
		}

		try
		{
			WorldGuardExtraFlagsPlugin.supportsMobEffectColors = PotionEffect.class.getDeclaredMethod("getColor", Color.class) != null;
		}
		catch (Throwable ignored)
		{
		}
		
		try
		{
			WorldGuardExtraFlagsPlugin.supportsStopSound = Player.class.getDeclaredMethod("stopSound", Color.class) != null;
		}
		catch (Throwable ignored)
		{
		}
	}
	
	@Override
	public void onLoad()
	{
		WorldGuardExtraFlagsPlugin.worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		
		WorldGuardExtraFlagsPlugin.worldGuardPlugin = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.TELEPORT_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.TELEPORT_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.COMMAND_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.COMMAND_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.CONSOLE_COMMAND_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.CONSOLE_COMMAND_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.WALK_SPEED);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.KEEP_INVENTORY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.KEEP_EXP);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.CHAT_PREFIX);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.CHAT_SUFFIX);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.BLOCKED_EFFECTS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.GODMODE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.RESPAWN_LOCATION);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.WORLDEDIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.GIVE_EFFECTS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.FLY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.PLAY_SOUNDS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.MYTHICMOB_EGGS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.FROSTWALKER);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.NETHER_PORTALS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.ALLOW_BLOCK_PLACE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.DENY_BLOCK_PLACE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.ALLOW_BLOCK_BREAK);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.DENY_BLOCK_BREAK);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.GLIDE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.CHUNK_UNLOAD);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(FlagUtils.ITEM_DURABILITY);
	}
	
	@Override
	public void onEnable()
	{
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(TeleportOnEntryFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(TeleportOnExitFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(CommandOnEntryFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(CommandOnExitFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(ConsoleCommandOnEntryFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(ConsoleCommandOnExitFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(WalkSpeedFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(BlockedEffectsFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GodmodeFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GiveEffectsFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(FlyFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(PlaySoundsFlag.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GlideFlag.FACTORY, null);
		
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(), this);
		
		try
		{
			if (EntityToggleGlideEvent.class != null) //LOL, Just making it look nice xD
			{
				this.getServer().getPluginManager().registerEvents(new EntityListenerOnePointNine(), this);
			}
		}
		catch(NoClassDefFoundError ignored)
		{
			
		}

		Plugin essentialsPlugin = this.getServer().getPluginManager().getPlugin("Essentials");
		if (essentialsPlugin != null)
		{
			WorldGuardExtraFlagsPlugin.essentialsPlugin = (Essentials)essentialsPlugin;
		}
		
		WorldGuardExtraFlagsPlugin.mythicMobsEnabled = this.getServer().getPluginManager().isPluginEnabled("MythicMobs");
		WorldGuardExtraFlagsPlugin.fastAsyncWorldEditEnabled = this.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
		
		if (WorldGuardExtraFlagsPlugin.fastAsyncWorldEditEnabled)
		{
			PluginUtils.registerFAWE();
		}
		else
		{
			WorldGuardExtraFlagsPlugin.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener());
		}
		
		if (WorldGuardExtraFlagsPlugin.isEssentialsEnable())
		{
			this.getServer().getPluginManager().registerEvents(new EssentialsListener(), this);
		}
		
		for(World world : this.getServer().getWorlds())
		{
			WorldGuardExtraFlagsPlugin.doUnloadChunkFlagWorldCheck(world);
		}
	}
	
	public static boolean isEssentialsEnable()
	{
		return WorldGuardExtraFlagsPlugin.essentialsPlugin != null;
	}
	
	public static void doUnloadChunkFlagWorldCheck(World world)
	{
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.worldGuardPlugin.getRegionManager(world).getRegions().values())
		{
			if (region.getFlag(FlagUtils.CHUNK_UNLOAD) == State.DENY)
			{
				WorldGuardExtraFlagsPlugin.getPlugin().getLogger().info("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");
				
				Location min = BukkitUtil.toLocation(world, region.getMinimumPoint());
				Location max = BukkitUtil.toLocation(world, region.getMaximumPoint());

				for(int x = min.getChunk().getX(); x <= max.getChunk().getX(); x++)
				{
					for(int z = min.getChunk().getZ(); z <= max.getChunk().getZ(); z++)
					{
						world.getChunkAt(x, z).load(true);
					}
				}
			}
		}
	}
}
