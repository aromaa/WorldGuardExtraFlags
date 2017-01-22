package net.goldtreeservers.worldguardextraflags;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.earth2me.essentials.Essentials;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.goldtreeservers.worldguardextraflags.flags.BlockedEffectsFlag;
import net.goldtreeservers.worldguardextraflags.flags.CommandOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.CommandOnExitFlag;
import net.goldtreeservers.worldguardextraflags.flags.ConsoleCommandOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.ConsoleCommandOnExitFlag;
import net.goldtreeservers.worldguardextraflags.flags.CustomSetFlag;
import net.goldtreeservers.worldguardextraflags.flags.FlyFlag;
import net.goldtreeservers.worldguardextraflags.flags.GiveEffectsFlag;
import net.goldtreeservers.worldguardextraflags.flags.GlideFlag;
import net.goldtreeservers.worldguardextraflags.flags.GodmodeFlag;
import net.goldtreeservers.worldguardextraflags.flags.MaterialFlag;
import net.goldtreeservers.worldguardextraflags.flags.PlaySoundsFlag;
import net.goldtreeservers.worldguardextraflags.flags.PotionEffectFlag;
import net.goldtreeservers.worldguardextraflags.flags.PotionEffectTypeFlag;
import net.goldtreeservers.worldguardextraflags.flags.SoundDataFlag;
import net.goldtreeservers.worldguardextraflags.flags.TeleportOnEntryFlag;
import net.goldtreeservers.worldguardextraflags.flags.TeleportOnExitFlag;
import net.goldtreeservers.worldguardextraflags.flags.WalkSpeedFlag;
import net.goldtreeservers.worldguardextraflags.listeners.BlockListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListener;
import net.goldtreeservers.worldguardextraflags.listeners.EntityListenerOnePointNine;
import net.goldtreeservers.worldguardextraflags.listeners.EssentialsListener;
import net.goldtreeservers.worldguardextraflags.listeners.PlayerListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldEditListener;
import net.goldtreeservers.worldguardextraflags.listeners.WorldListener;
import net.goldtreeservers.worldguardextraflags.utils.PluginUtils;
import net.goldtreeservers.worldguardextraflags.utils.SoundData;

public class WorldGuardExtraFlagsPlugin extends JavaPlugin
{
	private static WorldGuardExtraFlagsPlugin plugin;
	private static WorldGuardPlugin worldGuardPlugin;
	private static WorldEditPlugin worldEditPlugin;
	private static Essentials essentialsPlugin;
	private static boolean mythicMobsEnabled;
	private static boolean supportFrostwalker;
	private static boolean fastAsyncWorldEditEnabled;
	private static boolean essentialsEnabled;
	
	public final static LocationFlag teleportOnEntry = new LocationFlag("teleport-on-entry");
	public final static LocationFlag teleportOnExit = new LocationFlag("teleport-on-exit");
	public final static CustomSetFlag<String> commandOnEntry = new CustomSetFlag<String>("command-on-entry", new CommandStringFlag(null));
	public final static CustomSetFlag<String> commandOnExit = new CustomSetFlag<String>("command-on-exit", new CommandStringFlag(null));
	public final static CustomSetFlag<String> consoleCommandOnEntry = new CustomSetFlag<String>("console-command-on-entry", new CommandStringFlag(null));
	public final static CustomSetFlag<String> consoleCommandOnExit = new CustomSetFlag<String>("console-command-on-exit", new CommandStringFlag(null));
	public final static DoubleFlag walkSpeed = new DoubleFlag("walk-speed");
	public final static BooleanFlag keepInventory = new BooleanFlag("keep-inventory");
	public final static BooleanFlag keepExp = new BooleanFlag("keep-exp");
	public final static StringFlag chatPrefix = new StringFlag("chat-prefix");
	public final static StringFlag chatSuffix = new StringFlag("chat-suffix");
	public final static SetFlag<PotionEffectType> blockedEffects = new SetFlag<PotionEffectType>("blocked-effects", new PotionEffectTypeFlag(null));
	public final static StateFlag godmode = new StateFlag("godmode", false);
	public final static LocationFlag respawnLocation = new LocationFlag("respawn-location");
	public final static StateFlag worldEdit = new StateFlag("worldedit", true);
	public final static SetFlag<PotionEffect> giveEffects = new SetFlag<PotionEffect>("give-effects", new PotionEffectFlag(null));
	public final static StateFlag fly = new StateFlag("fly", false);
	public final static SetFlag<SoundData> playSounds = new SetFlag<SoundData>("play-sounds", new SoundDataFlag(null));
	public final static StateFlag mythicMobsEggs = new StateFlag("mythicmobs-eggs", true);
	public final static StateFlag frostwalker = new StateFlag("frostwalker", true);
	public final static StateFlag netherPortals = new StateFlag("nether-portals", true);
	public final static SetFlag<Material> allowBlockPlace = new SetFlag<Material>("allow-block-place", new MaterialFlag(null));
	public final static SetFlag<Material> denyBlockPlace = new SetFlag<Material>("deny-block-place", new MaterialFlag(null));
	public final static SetFlag<Material> allowBlockBreak = new SetFlag<Material>("allow-block-break", new MaterialFlag(null));
	public final static SetFlag<Material> denyBlockBreak = new SetFlag<Material>("deny-block-break", new MaterialFlag(null));
	public final static StateFlag glide = new StateFlag("glide", false);
	public final static StateFlag chunkUnload = new StateFlag("chunk-unload", true);
	public final static StateFlag itemDurability = new StateFlag("item-durability", true);
	
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
		catch (NoSuchFieldError ignored)
		{
			
		}
	}
	
	@Override
	public void onLoad()
	{
		WorldGuardExtraFlagsPlugin.worldEditPlugin = (WorldEditPlugin) this.getServer().getPluginManager().getPlugin("WorldEdit");
		
		WorldGuardExtraFlagsPlugin.worldGuardPlugin = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.teleportOnEntry);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.teleportOnExit);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.commandOnEntry);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.commandOnExit);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.consoleCommandOnEntry);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.consoleCommandOnExit);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.walkSpeed);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.keepInventory);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.keepExp);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.chatPrefix);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.chatSuffix);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.blockedEffects);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.godmode);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.respawnLocation);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.worldEdit);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.giveEffects);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.fly);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.playSounds);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.mythicMobsEggs);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.frostwalker);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.netherPortals);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.allowBlockPlace);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.denyBlockPlace);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.allowBlockBreak);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.denyBlockBreak);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.glide);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.chunkUnload);
		WorldGuardExtraFlagsPlugin.worldGuardPlugin.getFlagRegistry().register(WorldGuardExtraFlagsPlugin.itemDurability);
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
		WorldGuardExtraFlagsPlugin.essentialsEnabled = this.getServer().getPluginManager().isPluginEnabled("Essentials");
		
		if (WorldGuardExtraFlagsPlugin.fastAsyncWorldEditEnabled)
		{
			PluginUtils.registerFAWE();
		}
		else
		{
			WorldGuardExtraFlagsPlugin.worldEditPlugin.getWorldEdit().getEventBus().register(new WorldEditListener());
		}
		
		if (WorldGuardExtraFlagsPlugin.essentialsEnabled)
		{
			this.getServer().getPluginManager().registerEvents(new EssentialsListener(), this);
		}
		
		for(World world : this.getServer().getWorlds())
		{
			WorldGuardExtraFlagsPlugin.doUnloadChunkFlagWorldCheck(world);
		}
	}
	
	public static WorldGuardExtraFlagsPlugin getPlugin()
	{
		return WorldGuardExtraFlagsPlugin.plugin;
	}
	
	public static WorldGuardPlugin getWorldGuard()
	{
		return WorldGuardExtraFlagsPlugin.worldGuardPlugin;
	}
	
	public static WorldEditPlugin getWorldEditPlugin()
	{
		return WorldGuardExtraFlagsPlugin.worldEditPlugin;
	}
	
	public static boolean isMythicMobsEnabled()
	{
		return WorldGuardExtraFlagsPlugin.mythicMobsEnabled;
	}
	
	public static boolean isSupportingFrostwalker()
	{
		return WorldGuardExtraFlagsPlugin.supportFrostwalker;
	}
	
	public static boolean isFastAsyncWorldEditEnabled()
	{
		return WorldGuardExtraFlagsPlugin.fastAsyncWorldEditEnabled;
	}
	
	public static boolean isEssentialsEnabled()
	{
		return WorldGuardExtraFlagsPlugin.essentialsEnabled;
	}
	
	public static Essentials getEssentialsPlugin()
	{
		return WorldGuardExtraFlagsPlugin.essentialsPlugin;
	}
	
	public static void doUnloadChunkFlagWorldCheck(World world)
	{
		for (ProtectedRegion region : WorldGuardExtraFlagsPlugin.getWorldGuard().getRegionManager(world).getRegions().values())
		{
			if (region.getFlag(WorldGuardExtraFlagsPlugin.chunkUnload) == State.DENY)
			{
				System.out.println("Loading chunks for region " + region.getId() + " located in " + world.getName());
				
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
