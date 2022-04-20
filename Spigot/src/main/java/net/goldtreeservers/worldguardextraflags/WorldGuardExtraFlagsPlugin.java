package net.goldtreeservers.worldguardextraflags;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.SessionManager;
import net.goldtreeservers.worldguardextraflags.listeners.*;
import net.goldtreeservers.worldguardextraflags.wg.handlers.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.essentials.EssentialsHelper;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.protocollib.ProtocolLibHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	@Getter private static WorldGuardExtraFlagsPlugin plugin;
	
	@Getter private WorldGuardPlugin worldGuardPlugin;
	@Getter private WorldEditPlugin worldEditPlugin;

	@Getter private EssentialsHelper essentialsHelper;
	@Getter private ProtocolLibHelper protocolLibHelper;
	
	public WorldGuardExtraFlagsPlugin()
	{
		WorldGuardExtraFlagsPlugin.plugin = this;
	}
	
	@Override
	public void onLoad()
	{
		this.worldEditPlugin = (WorldEditPlugin)this.getServer().getPluginManager().getPlugin("WorldEdit");
		this.worldGuardPlugin = (WorldGuardPlugin)this.getServer().getPluginManager().getPlugin("WorldGuard");

		try
		{
			FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();
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
			flagRegistry.register(Flags.FLY_SPEED);
			flagRegistry.register(Flags.PLAY_SOUNDS);
			flagRegistry.register(Flags.FROSTWALKER);
			flagRegistry.register(Flags.NETHER_PORTALS);
			flagRegistry.register(Flags.GLIDE);
			flagRegistry.register(Flags.CHUNK_UNLOAD);
			flagRegistry.register(Flags.ITEM_DURABILITY);
			flagRegistry.register(Flags.JOIN_LOCATION);
		}
		catch (Exception e)
		{
			this.getServer().getPluginManager().disablePlugin(this);

			throw new RuntimeException("Failed to load WorldGuard communicator", e);
		}

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
			Plugin protocolLibPlugin = this.getServer().getPluginManager().getPlugin("ProtocolLib");
			if (protocolLibPlugin != null)
			{
				this.protocolLibHelper = new ProtocolLibHelper(this, protocolLibPlugin);
			}
		}
		catch(Throwable ignore)
		{
			
		}
	}
	
	@Override
	public void onEnable()
	{
		try
		{
			SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
			sessionManager.registerHandler(TeleportOnEntryFlagHandler.FACTORY(plugin), null);
			sessionManager.registerHandler(TeleportOnExitFlagHandler.FACTORY(plugin), null);
			sessionManager.registerHandler(CommandOnEntryFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(CommandOnExitFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(BlockedEffectsFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(GodmodeFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(GiveEffectsFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(FlyFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(FlySpeedFlagHandler.FACTORY(), null);
			sessionManager.registerHandler(PlaySoundsFlagHandler.FACTORY(plugin), null);
			sessionManager.registerHandler(GlideFlagHandler.FACTORY(), null);
		}
		catch (Exception e)
		{
			this.getServer().getPluginManager().disablePlugin(this);

			throw new RuntimeException("Failed to enable WorldGuard communicator", e);
		}

		WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this, platform.getRegionContainer(), platform.getSessionManager()), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(this, platform.getRegionContainer()), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);
		
		try
		{
			if (EntityToggleGlideEvent.class != null) //LOL, Just making it look nice xD
			{
				this.getServer().getPluginManager().registerEvents(new EntityListenerOnePointNine(this, platform.getRegionContainer()), this);
			}
		}
		catch(NoClassDefFoundError ignored)
		{
			
		}

		this.getServer().getPluginManager().registerEvents(new EntityListener(this, platform.getRegionContainer()), this);

		this.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener(this));
		
		if (this.essentialsHelper != null)
		{
			this.essentialsHelper.onEnable();
		}
		
		if (this.protocolLibHelper != null)
		{
			this.protocolLibHelper.onEnable();
		}
		else
		{
			this.getServer().getPluginManager().registerEvents(new EntityPotionEffectEventListener(this, platform.getSessionManager()), this);
		}
		
		for(World world : this.getServer().getWorlds())
		{
			this.doUnloadChunkFlagCheck(world);
		}
		
		this.setupMetrics();
	}

	public void doUnloadChunkFlagCheck(org.bukkit.World world)
	{
		for (ProtectedRegion region : WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegions().values())
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == StateFlag.State.DENY)
			{
				System.out.println("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");

				BlockVector3 min = region.getMinimumPoint();
				BlockVector3 max = region.getMaximumPoint();

				for(int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; x++)
				{
					for(int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++)
					{
						world.getChunkAt(x, z).load(true);
						world.getChunkAt(x, z).setForceLoaded(true);
					}
				}
			}
		}
	}
	
	private void setupMetrics()
	{
		final int bStatsPluginId = 7301;
		
        Metrics metrics = new Metrics(this, bStatsPluginId);
        metrics.addCustomChart(new Metrics.AdvancedPie("flags_count", new Callable<Map<String, Integer>>()
        {
        	private final Set<Flag<?>> flags = WorldGuardExtraFlagsPlugin.this.getPluginFlags();
        	
			@Override
			public Map<String, Integer> call() throws Exception
			{
	            Map<Flag<?>, Integer> valueMap = this.flags.stream().collect(Collectors.toMap((v) -> v, (v) -> 0));

				WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().forEach((m) ->
	            {
	            	m.getRegions().values().forEach((r) ->
	            	{
	            		r.getFlags().keySet().forEach((f) -> 
	            		{
	            			valueMap.computeIfPresent(f, (k, v) -> v + 1);
	            		});
	            	});
	            });
	            
				return valueMap.entrySet().stream().collect(Collectors.toMap((v) -> v.getKey().getName(), (v) -> v.getValue()));
			}
        }));
	}
	
	private Set<Flag<?>> getPluginFlags()
	{
		Set<Flag<?>> flags = new HashSet<>();
		
		for (Field field : Flags.class.getFields())
		{
			try
			{
				flags.add((Flag<?>)field.get(null));
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
			}
		}
		
		return flags;
	}
}
