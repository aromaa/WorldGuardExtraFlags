package net.goldtreeservers.worldguardextraflags.flags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import net.goldtreeservers.worldguardextraflags.utils.SoundData;

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
    
    private HashMap<ProtectedRegion, List<BukkitRunnable>> runnables = new HashMap<>();
	    
	protected PlaySoundsFlag(Session session)
	{
		super(session);
	}

	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		for(ProtectedRegion region : entered)
		{
			Set<SoundData> sounds = region.getFlag(WorldGuardExtraFlagsPlugin.playSounds);
			if (sounds != null)
			{
				List<BukkitRunnable> runnables = this.runnables.getOrDefault(region, new ArrayList<>());
				for(SoundData sound : sounds)
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
							
							try
							{
								player.stopSound(sound.getSound());
							}
							catch(Exception ex)
							{
								
							}
						}
					};
					
					runnables.add(runnable);
					runnable.runTaskTimer(WorldGuardExtraFlagsPlugin.getPlugin(), 0L, sound.getInterval());
				}
				
				this.runnables.put(region, runnables);
			}
		}
		
		for(ProtectedRegion region : exited)
		{
			List<BukkitRunnable> runnables = this.runnables.remove(region);
			if (runnables != null)
			{
				for(BukkitRunnable runnable : runnables)
				{
					runnable.cancel();
				}
			}
		}
		return true;
	}
	
	
}
