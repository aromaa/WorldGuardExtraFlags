package net.goldtreeservers.worldguardextraflags;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import net.goldtreeservers.worldguardextraflags.listeners.*;
import org.bstats.bukkit.Metrics;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.essentials.EssentialsHelper;
import net.goldtreeservers.worldguardextraflags.fawe.FAWEHelper;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.protocollib.ProtocolLibHelper;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.WorldGuardCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.v6.WorldGuardSixCommunicator;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.v7.WorldGuardSevenCommunicator;

public class WorldGuardExtraFlagsPlugin extends AbstractWorldGuardExtraFlagsPlugin
{
	@Getter private static WorldGuardExtraFlagsPlugin plugin;
	
	@Getter private WorldGuardPlugin worldGuardPlugin;
	@Getter private WorldEditPlugin worldEditPlugin;

	@Getter private EssentialsHelper essentialsHelper;
	@Getter private FAWEHelper faweHelper;
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
		
		this.worldGuardCommunicator = WorldGuardExtraFlagsPlugin.createWorldGuardCommunicator();
		if (this.worldGuardCommunicator == null)
		{
			throw new RuntimeException("Unsupported WorldGuard version: " + this.worldGuardPlugin.getDescription().getVersion());
		}
		
		WorldGuardUtils.setCommunicator(this.worldGuardCommunicator);
		
		try
		{
			this.worldGuardCommunicator.onLoad(this);
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
			Plugin fastAsyncWorldEditPlugin = this.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
			if (fastAsyncWorldEditPlugin != null)
			{
				this.faweHelper = new FAWEHelper(this, fastAsyncWorldEditPlugin);
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
		if (this.worldGuardCommunicator == null)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			
			return;
		}
		
		try
		{
			this.worldGuardCommunicator.onEnable(this);
		}
		catch (Exception e)
		{
			this.getServer().getPluginManager().disablePlugin(this);
			
			throw new RuntimeException("Failed to enable WorldGuard communicator", e);
		}

		this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		this.getServer().getPluginManager().registerEvents(new WorldListener(this), this);

		if (this.worldGuardCommunicator.isLegacy())
		{
			this.getServer().getPluginManager().registerEvents(new BlockListenerWG(this), this);
		}
		
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
		
		try
		{
			ParameterizedType type = (ParameterizedType)PortalCreateEvent.class.getDeclaredField("blocks").getGenericType();
			Class<?> clazz = (Class<?>)type.getActualTypeArguments()[0];
			if (clazz == BlockState.class)
			{
				this.getServer().getPluginManager().registerEvents(new net.goldtreeservers.worldguardextraflags.spigot1_14.EntityListener(this), this);
			}
			else
			{
				this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
			}
		}
		catch(Throwable ignored)
		{
			this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
		}
		
		if (this.faweHelper != null)
		{
			this.faweHelper.onEnable();
		}
		else
		{
			this.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener(this));
		}
		
		if (this.essentialsHelper != null)
		{
			this.essentialsHelper.onEnable();
		}
		
		if (this.protocolLibHelper != null)
		{
			this.protocolLibHelper.onEnable();
		}
		else if (SupportedFeatures.isPotionEffectEventSupported())
		{
			this.getServer().getPluginManager().registerEvents(new EntityPotionEffectEventListener(this), this);
		}
		
		for(World world : this.getServer().getWorlds())
		{
			this.getWorldGuardCommunicator().doUnloadChunkFlagCheck(world);
		}
		
		this.setupMetrics();
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

	            WorldGuardExtraFlagsPlugin.this.getWorldGuardCommunicator().getRegionContainer().getLoaded().forEach((m) ->
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
	
	public static WorldGuardCommunicator createWorldGuardCommunicator()
	{
		try
		{
			Class.forName("com.sk89q.worldguard.WorldGuard"); //Only exists in WG 7
			
			return new WorldGuardSevenCommunicator();
		}
		catch (Throwable ignored)
		{
			
		}
		
		try
		{
			Class<?> clazz = Class.forName("com.sk89q.worldguard.bukkit.WorldGuardPlugin");
			if (clazz.getMethod("getFlagRegistry") != null)
			{
				return new WorldGuardSixCommunicator();
			}
		}
		catch (Throwable ignored)
		{
			ignored.printStackTrace();
		}
		
		return null;
	}
}
