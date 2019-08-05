package net.goldtreeservers.worldguardextraflags;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.WorldGuardCommunicator;

public abstract class AbstractWorldGuardExtraFlagsPlugin extends JavaPlugin
{
	@Getter protected WorldGuardCommunicator worldGuardCommunicator;
}
