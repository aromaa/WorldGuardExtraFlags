package net.goldtreeservers.worldguardextraflags;

import org.bukkit.World;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.essentials.EssentialsUtils;
import net.goldtreeservers.worldguardextraflags.fawe.FAWEUtils;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.listeners.BlockListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListenerOnePointNine;
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

	@Getter private static boolean essentialsPluginEnabled;
	@Getter private static boolean mythicMobsPluginEnabled;
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
		WorldGuard.getInstance().getFlagRegistry().register(Flags.TELEPORT_ON_ENTRY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.TELEPORT_ON_EXIT);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.COMMAND_ON_ENTRY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.COMMAND_ON_EXIT);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.CONSOLE_COMMAND_ON_ENTRY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.CONSOLE_COMMAND_ON_EXIT);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.WALK_SPEED);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.KEEP_INVENTORY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.KEEP_EXP);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.CHAT_PREFIX);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.CHAT_SUFFIX);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.BLOCKED_EFFECTS);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.GODMODE);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.RESPAWN_LOCATION);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.WORLDEDIT);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.GIVE_EFFECTS);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.FLY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.PLAY_SOUNDS);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.MYTHICMOB_EGGS);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.FROSTWALKER);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.NETHER_PORTALS);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.ALLOW_BLOCK_PLACE);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.DENY_BLOCK_PLACE);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.ALLOW_BLOCK_BREAK);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.DENY_BLOCK_BREAK);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.GLIDE);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.CHUNK_UNLOAD);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.ITEM_DURABILITY);
		WorldGuard.getInstance().getFlagRegistry().register(Flags.JOIN_LOCATION);

		//Soft dependencies, due to some compatibility issues or add flags related to a plugin
		try
		{
			Plugin essentialsPlugin = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPluginManager().getPlugin("Essentials");
			if (essentialsPlugin != null)
			{
				EssentialsUtils.onLoad(essentialsPlugin);
				
				WorldGuardExtraFlagsPlugin.essentialsPluginEnabled = true;
			}
		}
		catch(Throwable ex)
		{
			
		}

		try
		{
			Plugin fastAsyncWorldEditPlugin = this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
			if (fastAsyncWorldEditPlugin != null)
			{
				FAWEUtils.onLoad(fastAsyncWorldEditPlugin);
				
				WorldGuardExtraFlagsPlugin.fastAsyncWorldEditPluginEnabled = true;
			}
		}
		catch(Throwable ex)
		{
			
		}
	}
	
	@Override
	public void onEnable()
	{
		getSessionManager().registerHandler(TeleportOnEntryFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(TeleportOnExitFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(CommandOnEntryFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(CommandOnExitFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(WalkSpeedFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(BlockedEffectsFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(GodmodeFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(GiveEffectsFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(FlyFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(PlaySoundsFlagHandler.FACTORY, null);
		getSessionManager().registerHandler(GlideFlagHandler.FACTORY, null);
		
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
		
		if (WorldGuardExtraFlagsPlugin.isFastAsyncWorldEditPluginEnabled())
		{
			FAWEUtils.onEnable();
		}
		else
		{
			WorldGuardExtraFlagsPlugin.getWorldEditPlugin().getWorldEdit().getEventBus().register(new WorldEditListener());
		}
		
		if (WorldGuardExtraFlagsPlugin.isEssentialsPluginEnabled())
		{
			EssentialsUtils.onEnable();
		}
		
		for(World world : this.getServer().getWorlds())
		{
			WorldUtils.doUnloadChunkFlagCheck(world);
		}
	}

	public static RegionManager getRegionManager(World world) {
		return getRegionContainer().get(BukkitAdapter.adapt(world));
	}

	public static RegionContainer getRegionContainer() {
		return WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	public static SessionManager getSessionManager() {
		return WorldGuard.getInstance().getPlatform().getSessionManager();
	}
}
