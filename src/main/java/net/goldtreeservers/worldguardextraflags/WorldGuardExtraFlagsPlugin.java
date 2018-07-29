package net.goldtreeservers.worldguardextraflags;

import org.bukkit.World;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.essentials.EssentialsHelper;
import net.goldtreeservers.worldguardextraflags.fawe.FAWEHelper;
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
import net.goldtreeservers.worldguardextraflags.wg.legacy.WorldGuardCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.legacy.wrappers.SessionManagerWrapper;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	@Getter private static WorldGuardExtraFlagsPlugin plugin;
	
	@Getter private WorldGuardPlugin worldGuardPlugin;
	@Getter private WorldEditPlugin worldEditPlugin;
	
	@Getter private WorldGuardCommunicator worldGuardCommunicator;

	@Getter private EssentialsHelper essentialsHelper;
	@Getter private FAWEHelper faweHelper;
	
	public WorldGuardExtraFlagsPlugin()
	{
		WorldGuardExtraFlagsPlugin.plugin = this;
	}
	
	@Override
	public void onLoad()
	{
		this.worldEditPlugin = (WorldEditPlugin)this.getServer().getPluginManager().getPlugin("WorldEdit");
		this.worldGuardPlugin = (WorldGuardPlugin)this.getServer().getPluginManager().getPlugin("WorldGuard");
		
		this.worldGuardCommunicator = WorldGuardCommunicator.create();
		if (this.worldGuardCommunicator == null)
		{
			throw new RuntimeException("Unsupported WorldGuard version: " + this.worldGuardPlugin.getDescription().getVersion());
		}
		
		try
		{
			this.worldGuardCommunicator.onLoad();
		}
		catch (Exception e)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			
			throw new RuntimeException("Failed to load WorldGuard communicator", e);
		}
		
		FlagRegistry flagRegistry = this.worldGuardCommunicator.getFlagRegistry();
		flagRegistry.register(Flags.TELEPORT_ON_ENTRY);
		flagRegistry.register(Flags.TELEPORT_ON_EXIT);
		flagRegistry.register(Flags.COMMAND_ON_ENTRY);
		flagRegistry.register(Flags.COMMAND_ON_EXIT);
		flagRegistry.register(Flags.CONSOLE_COMMAND_ON_ENTRY);
		flagRegistry.register(Flags.CONSOLE_COMMAND_ON_EXIT);
		flagRegistry.register(Flags.WALK_SPEED);
		flagRegistry.register(Flags.KEEP_INVENTORY);
		flagRegistry.register(Flags.KEEP_EXP);
		flagRegistry.register(Flags.CHAT_PREFIX);
		flagRegistry.register(Flags.CHAT_SUFFIX);
		flagRegistry.register(Flags.BLOCKED_EFFECTS);
		flagRegistry.register(Flags.GODMODE);
		flagRegistry.register(Flags.RESPAWN_LOCATION);
		flagRegistry.register(Flags.WORLDEDIT);
		flagRegistry.register(Flags.GIVE_EFFECTS);
		flagRegistry.register(Flags.FLY);
		flagRegistry.register(Flags.PLAY_SOUNDS);
		flagRegistry.register(Flags.MYTHICMOB_EGGS);
		flagRegistry.register(Flags.FROSTWALKER);
		flagRegistry.register(Flags.NETHER_PORTALS);
		flagRegistry.register(Flags.ALLOW_BLOCK_PLACE);
		flagRegistry.register(Flags.DENY_BLOCK_PLACE);
		flagRegistry.register(Flags.ALLOW_BLOCK_BREAK);
		flagRegistry.register(Flags.DENY_BLOCK_BREAK);
		flagRegistry.register(Flags.GLIDE);
		flagRegistry.register(Flags.CHUNK_UNLOAD);
		flagRegistry.register(Flags.ITEM_DURABILITY);
		flagRegistry.register(Flags.JOIN_LOCATION);

		//Soft dependencies, due to some compatibility issues or add flags related to a plugin
		try
		{
			Plugin essentialsPlugin = WorldGuardExtraFlagsPlugin.getPlugin().getServer().getPluginManager().getPlugin("Essentials");
			if (essentialsPlugin != null)
			{
				this.essentialsHelper = new EssentialsHelper(this, essentialsPlugin);
			}
		}
		catch(Throwable ignore)
		{
			
		}

		try
		{
			Plugin fastAsyncWorldEditPlugin = this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
			if (fastAsyncWorldEditPlugin != null)
			{
				this.faweHelper = new FAWEHelper(this, fastAsyncWorldEditPlugin);
			}
		}
		catch(Throwable ignore)
		{
			
		}
	}
	
	@Override
	public void onEnable()
	{
		if (this.worldGuardCommunicator == null)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			
			return;
		}
		
		try
		{
			this.worldGuardCommunicator.onEnable();
		}
		catch (Exception e)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			
			throw new RuntimeException("Failed to enable WorldGuard communicator", e);
		}
		
		SessionManagerWrapper sessionManager = this.worldGuardCommunicator.getSessionManager();
		sessionManager.registerHandler(TeleportOnEntryFlagHandler.FACTORY);
		sessionManager.registerHandler(TeleportOnExitFlagHandler.FACTORY);
		sessionManager.registerHandler(CommandOnEntryFlagHandler.FACTORY);
		sessionManager.registerHandler(CommandOnExitFlagHandler.FACTORY);
		sessionManager.registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY);
		sessionManager.registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY);
		sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY);
		sessionManager.registerHandler(BlockedEffectsFlagHandler.FACTORY);
		sessionManager.registerHandler(GodmodeFlagHandler.FACTORY);
		sessionManager.registerHandler(GiveEffectsFlagHandler.FACTORY);
		sessionManager.registerHandler(FlyFlagHandler.FACTORY);
		sessionManager.registerHandler(PlaySoundsFlagHandler.FACTORY);
		sessionManager.registerHandler(GlideFlagHandler.FACTORY);

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);
		
		try
		{
			if (EntityToggleGlideEvent.class != null) //LOL, Just making it look nice xD
			{
				this.getServer().getPluginManager().registerEvents(new EntityListenerOnePointNine(this), this);
			}
		}
		catch(NoClassDefFoundError ignored)
		{
			
		}
		
		if (this.faweHelper != null)
		{
			this.faweHelper.onEnable();
		}
		else
		{
			this.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener());
		}
		
		if (this.essentialsHelper != null)
		{
			this.essentialsHelper.onEnable();
		}
		
		for(World world : this.getServer().getWorlds())
		{
			WorldUtils.doUnloadChunkFlagCheck(world);
		}
	}
}
