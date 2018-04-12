package net.goldtreeservers.worldguardextraflags;

import org.bukkit.World;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import lombok.Getter;
import net.elseland.xikage.MythicMobs.MythicMobs;
import net.goldtreeservers.worldguardextraflags.fawe.FAWEUtils;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.listeners.BlockListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListenerOnePointNine;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;
import net.goldtreeservers.worldguardextraflags.listeners.PlayerListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldEditListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldListener;
import net.goldtreeservers.worldguardextraflags.utils.WorldUtils;
import net.goldtreeservers.worldguardextraflags.wg.handlers.BlockedEffectsFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.CommandOnEntryFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.CommandOnExitFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.ConsoleCommandOnEntryFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.ConsoleCommandOnExitFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.FlyFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GiveEffectsFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GlideFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.GodmodeFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.PlaySoundsFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.TeleportOnEntryFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.TeleportOnExitFlagHandler;
import net.goldtreeservers.worldguardextraflags.wg.handlers.WalkSpeedFlagHandler;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	@Getter private static WorldGuardExtraFlagsPlugin plugin;
	
	@Getter private static WorldGuardPlugin worldGuardPlugin;
	@Getter private static WorldEditPlugin worldEditPlugin;
	@Getter private static Essentials essentialsPlugin;
	@Getter private static MythicMobs mythicMobsPlugin;
	
	@Getter private static boolean fastAsyncWorldEditPluginEnabled;
	
	public WorldGuardExtraFlagsPlugin()
	{
		WorldGuardExtraFlagsPlugin.plugin = this;
	}
	
	@Override
	public void onLoad()
	{
		WorldGuardExtraFlagsPlugin.worldEditPlugin = (WorldEditPlugin)this.getServer().getPluginManager().getPlugin("WorldEdit");
		
		WorldGuardExtraFlagsPlugin.worldGuardPlugin = (WorldGuardPlugin)this.getServer().getPluginManager().getPlugin("WorldGuard");
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.TELEPORT_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.TELEPORT_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.COMMAND_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.COMMAND_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.CONSOLE_COMMAND_ON_ENTRY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.CONSOLE_COMMAND_ON_EXIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.WALK_SPEED);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.KEEP_INVENTORY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.KEEP_EXP);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.CHAT_PREFIX);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.CHAT_SUFFIX);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.BLOCKED_EFFECTS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.GODMODE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.RESPAWN_LOCATION);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.WORLDEDIT);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.GIVE_EFFECTS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.FLY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.PLAY_SOUNDS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.MYTHICMOB_EGGS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.FROSTWALKER);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.NETHER_PORTALS);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.ALLOW_BLOCK_PLACE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.DENY_BLOCK_PLACE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.ALLOW_BLOCK_BREAK);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.DENY_BLOCK_BREAK);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.GLIDE);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.CHUNK_UNLOAD);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.ITEM_DURABILITY);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(Flags.JOIN_LOCATION);

		//Soft dependencies, due to some compatibility issues or add flags related to a plugin
		Plugin essentialsPlugin = this.getServer().getPluginManager().getPlugin("Essentials");
		if (essentialsPlugin != null)
		{
			WorldGuardExtraFlagsPlugin.essentialsPlugin = (Essentials)essentialsPlugin;
		}
		
		Plugin mythicMobsPlugin = this.getServer().getPluginManager().getPlugin("MythicMobs");
		if (mythicMobsPlugin != null)
		{
			WorldGuardExtraFlagsPlugin.mythicMobsPlugin = (MythicMobs)mythicMobsPlugin;
		}
		
		WorldGuardExtraFlagsPlugin.fastAsyncWorldEditPluginEnabled = this.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
	}
	
	@Override
	public void onEnable()
	{
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(TeleportOnEntryFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(TeleportOnExitFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(CommandOnEntryFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(CommandOnExitFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(WalkSpeedFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(BlockedEffectsFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GodmodeFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GiveEffectsFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(FlyFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(PlaySoundsFlagHandler.FACTORY, null);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getSessionManager().registerHandler(GlideFlagHandler.FACTORY, null);
		
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
		
		if (WorldGuardExtraFlagsPlugin.fastAsyncWorldEditPluginEnabled)
		{
			FAWEUtils.registerFAWE();
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
			WorldUtils.doUnloadChunkFlagCheck(world);
		}
	}
	
	public static boolean isEssentialsEnable()
	{
		return WorldGuardExtraFlagsPlugin.essentialsPlugin != null;
	}
	
	public static boolean isMythicMobsPluginEnabled()
	{
		return WorldGuardExtraFlagsPlugin.essentialsPlugin != null;
	}
}
