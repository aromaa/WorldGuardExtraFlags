package net.goldtreeservers.worldguardextraflags.flags.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.SoundData;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class PlaySoundsFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<PlaySoundsFlag>
    {
        @Override
        public PlaySoundsFlag create(Session session)
        {
            return new PlaySoundsFlag(session);
        }
    }

    private HashMap<String, BukkitRunnable> runnables;
	    
	protected PlaySoundsFlag(Session session)
	{
		super(session);
		
		this.runnables = new HashMap<>();
	}

	@Override
	public void initialize(Player player, Location current, ApplicableRegionSet set)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			this.check(player, set);
		}
    }
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			this.check(player, toSet);
		}
		
		return true;
	}
	
	public void tick(Player player, ApplicableRegionSet set)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			this.check(player, set);
		}
    }
	
	private void check(Player player, ApplicableRegionSet set)
	{
		Set<SoundData> soundData = set.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.PLAY_SOUNDS);
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
							
							if (WorldGuardExtraFlagsPlugin.isSupportsStopSound())
							{
								player.stopSound(sound.getSound());
							}
						}
					};
	
					this.runnables.put(sound.getSound(), runnable);
					runnable.runTaskTimer(WorldGuardExtraFlagsPlugin.getPlugin(), 0L, sound.getInterval());
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
