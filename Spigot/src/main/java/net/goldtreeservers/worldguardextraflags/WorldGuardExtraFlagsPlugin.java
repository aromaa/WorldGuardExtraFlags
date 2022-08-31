package net.goldtreeservers.worldguardextraflags;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import net.goldtreeservers.worldguardextraflags.listeners.*;
import net.goldtreeservers.worldguardextraflags.wg.handlers.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.protocollib.ProtocolLibHelper;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	private static final Set<Flag<?>> FLAGS = WorldGuardExtraFlagsPlugin.getPluginFlags();
	@Getter private static WorldGuardExtraFlagsPlugin plugin;

	@Getter private WorldEditPlugin worldEditPlugin;

	@Getter private WorldGuardPlugin worldGuardPlugin;
	@Getter private WorldGuard worldGuard;

	@Getter private RegionContainer regionContainer;
	@Getter private SessionManager sessionManager;

	@Getter private ProtocolLibHelper protocolLibHelper;
	
	public WorldGuardExtraFlagsPlugin()
	{
		WorldGuardExtraFlagsPlugin.plugin = this;
	}
	
	@Override
	public void onLoad()
	{
		this.worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		this.worldGuardPlugin = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");

		this.worldGuard = WorldGuard.getInstance();

		try
		{
			FlagRegistry flagRegistry = this.worldGuard.getFlagRegistry();
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

			throw new RuntimeException(e instanceof IllegalStateException ?
					"WorldGuard prevented flag registration. Did you reload the plugin? This is not supported!" :
					"Flag registration failed!", e);
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
		this.regionContainer = this.worldGuard.getPlatform().getRegionContainer();
		this.sessionManager = this.worldGuard.getPlatform().getSessionManager();

		this.sessionManager.registerHandler(TeleportOnEntryFlagHandler.FACTORY(plugin), null);
		this.sessionManager.registerHandler(TeleportOnExitFlagHandler.FACTORY(plugin), null);
		this.sessionManager.registerHandler(CommandOnEntryFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(CommandOnExitFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(ConsoleCommandOnEntryFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(ConsoleCommandOnExitFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(WalkSpeedFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(BlockedEffectsFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(GodmodeFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(GiveEffectsFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(FlyFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(FlySpeedFlagHandler.FACTORY(), null);
		this.sessionManager.registerHandler(PlaySoundsFlagHandler.FACTORY(plugin), null);
		this.sessionManager.registerHandler(GlideFlagHandler.FACTORY(), null);

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this, this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(this, this.regionContainer), this);
		this.getServer().getPluginManager().registerEvents(new EntityListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager), this);

		this.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener(this.worldGuardPlugin, this.regionContainer, this.sessionManager));
		
		if (this.protocolLibHelper != null)
		{
			this.protocolLibHelper.onEnable();
		}
		else
		{
			this.getServer().getPluginManager().registerEvents(new EntityPotionEffectEventListener(this.worldGuardPlugin, this.sessionManager), this);
		}
		
		for(World world : this.getServer().getWorlds())
		{
			this.doUnloadChunkFlagCheck(world);
		}
		
		this.setupMetrics();
	}

	public void doUnloadChunkFlagCheck(org.bukkit.World world)
	{
		RegionManager regionManager = this.regionContainer.get(BukkitAdapter.adapt(world));
		if (regionManager == null)
		{
			return;
		}

		for (ProtectedRegion region : regionManager.getRegions().values())
		{
			if (region.getFlag(Flags.CHUNK_UNLOAD) == StateFlag.State.DENY)
			{
				this.getLogger().info("Loading chunks for region " + region.getId() + " located in " + world.getName() + " due to chunk-unload flag being deny");

				BlockVector3 min = region.getMinimumPoint();
				BlockVector3 max = region.getMaximumPoint();

				for(int x = min.getBlockX() >> 4; x <= max.getBlockX() >> 4; x++)
				{
					for(int z = min.getBlockZ() >> 4; z <= max.getBlockZ() >> 4; z++)
					{
						world.getChunkAt(x, z).addPluginChunkTicket(this);
					}
				}
			}
		}
	}
	
	private void setupMetrics()
	{
		final int bStatsPluginId = 7301;
		
        Metrics metrics = new Metrics(this, bStatsPluginId);
        metrics.addCustomChart(new Metrics.AdvancedPie("flags_count", () ->
		{
			Map<Flag<?>, Integer> valueMap = WorldGuardExtraFlagsPlugin.FLAGS.stream().collect(Collectors.toMap(v -> v, v -> 0));

			WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().forEach(m ->
			{
				m.getRegions().values().forEach(r ->
				{
					r.getFlags().keySet().forEach(f -> valueMap.computeIfPresent(f, (k, v) -> 1));
				});
			});

			return valueMap.entrySet().stream().collect(Collectors.toMap(v -> v.getKey().getName(), v -> v.getValue()));
		}));
	}
	
	private static Set<Flag<?>> getPluginFlags()
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
