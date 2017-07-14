package net.goldtreeservers.worldguardextraflags.flags.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;

import net.goldtreeservers.worldguardextraflags.WorldGuardExtraFlagsPlugin;
import net.goldtreeservers.worldguardextraflags.helpers.PotionEffectDetails;
import net.goldtreeservers.worldguardextraflags.utils.FlagUtils;
import net.goldtreeservers.worldguardextraflags.utils.TimeUtils;
import net.goldtreeservers.worldguardextraflags.utils.WorldGuardUtils;

public class BlockedEffectsFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<BlockedEffectsFlag>
    {
        @Override
        public BlockedEffectsFlag create(Session session)
        {
            return new BlockedEffectsFlag(session);
        }
    }
	
	private HashMap<PotionEffectType, PotionEffectDetails> removedEffects;
    
	protected BlockedEffectsFlag(Session session)
	{
		super(session);
		
		this.removedEffects = new HashMap<>();
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
	
	@Override
	public void tick(Player player, ApplicableRegionSet set)
	{
		if (!WorldGuardUtils.hasBypass(player))
		{
			this.check(player, set);
		}
	}
	
	private void check(Player player, ApplicableRegionSet set)
	{
		Set<PotionEffectType> potionEffects = set.queryValue(WorldGuardUtils.wrapPlayer(player), FlagUtils.BLOCKED_EFFECTS);
		if (potionEffects != null && potionEffects.size() > 0)
		{
			for (PotionEffectType effectType : potionEffects)
			{
				PotionEffect effect = null;
				for(PotionEffect activeEffect : player.getActivePotionEffects())
				{
					if (activeEffect.getType().equals(effectType))
					{
						effect = activeEffect;
						break;
					}
				}
				
				if (effect != null)
				{
					if (WorldGuardExtraFlagsPlugin.isSupportsMobEffectColors())
					{
						this.removedEffects.put(effect.getType(), new PotionEffectDetails(TimeUtils.getUnixtimestamp() + effect.getDuration() / 20, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.getColor()));
					}
					else
					{
						this.removedEffects.put(effect.getType(), new PotionEffectDetails(TimeUtils.getUnixtimestamp() + effect.getDuration() / 20, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), null));
					}
					
					player.removePotionEffect(effectType);
				}
			}
		}
		
		Iterator<Entry<PotionEffectType, PotionEffectDetails>>  potionEffects_ = this.removedEffects.entrySet().iterator();
		while (potionEffects_.hasNext())
		{
			Entry<PotionEffectType, PotionEffectDetails> potionEffect = potionEffects_.next();
			
			if (potionEffects == null || !potionEffects.contains(potionEffect.getKey()))
			{
				PotionEffectDetails removedEffect = potionEffect.getValue();
				if (removedEffect != null)
				{
					int timeLeft = removedEffect.getTimeLeftInTicks();
					if (timeLeft > 0)
					{
						if (WorldGuardExtraFlagsPlugin.isSupportsMobEffectColors())
						{
							player.addPotionEffect(new PotionEffect(potionEffect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles(), removedEffect.getColor()), true);
						}
						else
						{
							player.addPotionEffect(new PotionEffect(potionEffect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles()), true);
						}
					}
				}
				
				potionEffects_.remove();
			}
		}
	}
}
