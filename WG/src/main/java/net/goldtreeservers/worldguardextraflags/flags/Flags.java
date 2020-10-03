package net.goldtreeservers.worldguardextraflags.flags;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.CommandStringFlag;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;

import net.goldtreeservers.worldguardextraflags.flags.data.SoundData;
import net.goldtreeservers.worldguardextraflags.flags.helpers.BlockMaterialFlag;
import net.goldtreeservers.worldguardextraflags.flags.helpers.ForcedStateFlag;
import net.goldtreeservers.worldguardextraflags.flags.helpers.PotionEffectFlag;
import net.goldtreeservers.worldguardextraflags.flags.helpers.PotionEffectTypeFlag;
import net.goldtreeservers.worldguardextraflags.flags.helpers.SoundDataFlag;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;

public final class Flags
{
	public final static LocationFlag TELEPORT_ON_ENTRY = new LocationFlag("teleport-on-entry");
	public final static LocationFlag TELEPORT_ON_EXIT = new LocationFlag("teleport-on-exit");
	
	public final static SetFlag<String> COMMAND_ON_ENTRY = WorldGuardUtils.getCommunicator().getCustomSetFlag("command-on-entry", new CommandStringFlag(null));
	public final static SetFlag<String> COMMAND_ON_EXIT = WorldGuardUtils.getCommunicator().getCustomSetFlag("command-on-exit", new CommandStringFlag(null));
	
	public final static SetFlag<String> CONSOLE_COMMAND_ON_ENTRY = WorldGuardUtils.getCommunicator().getCustomSetFlag("console-command-on-entry", new CommandStringFlag(null));
	public final static SetFlag<String> CONSOLE_COMMAND_ON_EXIT = WorldGuardUtils.getCommunicator().getCustomSetFlag("console-command-on-exit", new CommandStringFlag(null));
	
	public final static DoubleFlag WALK_SPEED = new DoubleFlag("walk-speed");
	public final static DoubleFlag FLY_SPEED = new DoubleFlag("fly-speed");
	
	public final static BooleanFlag KEEP_INVENTORY = new BooleanFlag("keep-inventory");
	public final static BooleanFlag KEEP_EXP = new BooleanFlag("keep-exp");
	
	public final static StringFlag CHAT_PREFIX = new StringFlag("chat-prefix");
	public final static StringFlag CHAT_SUFFIX = new StringFlag("chat-suffix");
	
	public final static SetFlag<PotionEffectType> BLOCKED_EFFECTS = new SetFlag<PotionEffectType>("blocked-effects", new PotionEffectTypeFlag(null));
	
	public final static StateFlag GODMODE = new StateFlag("godmode", false);
	
	public final static LocationFlag RESPAWN_LOCATION = new LocationFlag("respawn-location");
	
	public final static StateFlag WORLDEDIT = new StateFlag("worldedit", true);
	
	public final static SetFlag<PotionEffect> GIVE_EFFECTS = new SetFlag<PotionEffect>("give-effects", new PotionEffectFlag(null));
	
	public final static StateFlag FLY = new StateFlag("fly", false);
	
	public final static SetFlag<SoundData> PLAY_SOUNDS = new SetFlag<SoundData>("play-sounds", new SoundDataFlag(null));
	
	public final static StateFlag MYTHICMOB_EGGS = new StateFlag("mythicmobs-eggs", true);
	
	public final static StateFlag FROSTWALKER = new StateFlag("frostwalker", true);
	
	public final static StateFlag NETHER_PORTALS = new StateFlag("nether-portals", true);
	
	public final static SetFlag<Material> ALLOW_BLOCK_PLACE = new SetFlag<Material>("allow-block-place", new BlockMaterialFlag(null));
	public final static SetFlag<Material> DENY_BLOCK_PLACE = new SetFlag<Material>("deny-block-place", new BlockMaterialFlag(null));
	public final static SetFlag<Material> ALLOW_BLOCK_BREAK = new SetFlag<Material>("allow-block-break", new BlockMaterialFlag(null));
	public final static SetFlag<Material> DENY_BLOCK_BREAK = new SetFlag<Material>("deny-block-break", new BlockMaterialFlag(null));
	
	public final static ForcedStateFlag GLIDE = new ForcedStateFlag("glide");
	
	public final static StateFlag CHUNK_UNLOAD = new StateFlag("chunk-unload", true);
	
	public final static StateFlag ITEM_DURABILITY = new StateFlag("item-durability", true);
	
	public final static LocationFlag JOIN_LOCATION = new LocationFlag("join-location");
}
