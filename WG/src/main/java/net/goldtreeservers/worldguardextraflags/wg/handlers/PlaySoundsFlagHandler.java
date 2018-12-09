package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.SoundData;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class PlaySoundsFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<PlaySoundsFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public PlaySoundsFlagHandler create(Session session)
        {
            return new PlaySoundsFlagHandler(this.getPlugin(), session);
        }
    }

    private Map<String, BukkitRunnable> runnables;
	    
	protected PlaySoundsFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
		
		this.runnables = new HashMap<>();
	}

	@Override
	public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		this.check(player, set);
    }
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		this.check(player, toSet);
		
		return true;
	}
	
	public void tick(Player player, ApplicableRegionSet set)
	{
		this.check(player, set);
    }
	
	private void check(Player player, ApplicableRegionSet set)
	{
		Set<SoundData> soundData = WorldGuardUtils.queryValue(player, player.getWorld(), set.getRegions(), Flags.PLAY_SOUNDS);
		if (soundData != null && soundData.size() > 0)
		{
			for(SoundData sound : soundData)
			{
				if (!this.runnables.containsKey(sound.getSound()))
				{
					BukkitRunnable runnable = new BukkitRunnable()
					{
						@Override
						public void run()
						{
							player.playSound(player.getLocation(), sound.getSound(), Float.MAX_VALUE, 1);
						}
						
						@Override
						public void cancel()
						{
							super.cancel();
							
							if (SupportedFeatures.isStopSoundSupported())
							{
								player.stopSound(sound.getSound());
							}
						}
					};
	
					this.runnables.put(sound.getSound(), runnable);
					
					runnable.runTaskTimer(this.getPlugin(), 0L, sound.getInterval());
				}
			}
		}
		
		Iterator<Entry<String, BukkitRunnable>> runnables = this.runnables.entrySet().iterator();
		while (runnables.hasNext())
		{
			Entry<String, BukkitRunnable> runnable = runnables.next();
			
			if (soundData != null && soundData.size() > 0)
			{
				boolean skip = false;
				for(SoundData sound : soundData)
				{
					if (sound.getSound().equals(runnable.getKey()))
					{
						skip = true;
						break;
					}
				}
				
				if (skip)
				{
					continue;
				}
			}
			
			runnable.getValue().cancel();
			
			runnables.remove();
		}
	}
}
