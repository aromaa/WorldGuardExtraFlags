package net.goldtreeservers.worldguardextraflags.wg.handlers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;

import lombok.Getter;
import net.goldtreeservers.worldguardextraflags.flags.Flags;
import net.goldtreeservers.worldguardextraflags.flags.data.PotionEffectDetails;
import net.goldtreeservers.worldguardextraflags.utils.SupportedFeatures;
import net.goldtreeservers.worldguardextraflags.wg.WorldGuardUtils;
import net.goldtreeservers.worldguardextraflags.wg.wrappers.HandlerWrapper;

public class GiveEffectsFlagHandler extends HandlerWrapper
{
	public static final Factory FACTORY(Plugin plugin)
	{
		return new Factory(plugin);
	}
	
    public static class Factory extends HandlerWrapper.Factory<GiveEffectsFlagHandler>
    {
        public Factory(Plugin plugin)
        {
			super(plugin);
		}

		@Override
        public GiveEffectsFlagHandler create(Session session)
        {
            return new GiveEffectsFlagHandler(this.getPlugin(), session);
        }
    }

	private Map<PotionEffectType, PotionEffectDetails> removedEffects;
    private Set<PotionEffectType> givenEffects;
    
    @Getter private boolean supressRemovePotionPacket;
    
	protected GiveEffectsFlagHandler(Plugin plugin, Session session)
	{
		super(plugin, session);
		
		this.removedEffects = new HashMap<>();
		this.givenEffects = new HashSet<>();
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
	
	@Override
	public void tick(Player player, ApplicableRegionSet set)
	{
		this.check(player, set);
	}
	
	private void check(Player player, ApplicableRegionSet set)
	{
		Set<PotionEffect> potionEffects = WorldGuardUtils.queryValue(player, player.getWorld(), set.getRegions(), Flags.GIVE_EFFECTS);
		if (potionEffects != null && potionEffects.size() > 0)
		{
			try
			{
				for (PotionEffect effect : potionEffects)
				{
					PotionEffect effect_ = null;
					for(PotionEffect activeEffect : player.getActivePotionEffects())
					{
						if (activeEffect.getType().equals(effect.getType()))
						{
							effect_ = activeEffect;
							break;
						}
					}
					
					this.supressRemovePotionPacket = effect_ != null && effect_.getAmplifier() == effect.getAmplifier();
	
					if (this.givenEffects.add(effect.getType()) && effect_ != null)
					{
						this.removedEffects.put(effect_.getType(), new PotionEffectDetails(System.nanoTime() + (long)(effect_.getDuration() / 20D * TimeUnit.SECONDS.toNanos(1L)), effect_.getAmplifier(), effect_.isAmbient(), SupportedFeatures.isPotionEffectParticles() ? effect_.hasParticles() : true));
						
						player.removePotionEffect(effect_.getType());
					}
					
					player.addPotionEffect(effect, true);
				}
			}
			finally
			{
				this.supressRemovePotionPacket = false;
			}
		}
		
		Iterator<PotionEffectType> effectTypes = this.givenEffects.iterator();
		while (effectTypes.hasNext())
		{
			PotionEffectType type = effectTypes.next();
			
			if (potionEffects != null && potionEffects.size() > 0)
			{
				boolean skip = false;
				for (PotionEffect effect : potionEffects)
				{
					if (effect.getType().equals(type))
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
			
			player.removePotionEffect(type);
			
			effectTypes.remove();
		}
		
		Iterator<Entry<PotionEffectType, PotionEffectDetails>> potionEffects_ = this.removedEffects.entrySet().iterator();
		while (potionEffects_.hasNext())
		{
			Entry<PotionEffectType, PotionEffectDetails> effect = potionEffects_.next();
			if (!this.givenEffects.contains(effect.getKey()))
			{
				PotionEffectDetails removedEffect = effect.getValue();
				if (removedEffect != null)
				{
					int timeLeft = removedEffect.getTimeLeftInTicks();
					
					if (timeLeft > 0)
					{
						if (SupportedFeatures.isPotionEffectParticles())
						{
							player.addPotionEffect(new PotionEffect(effect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient(), removedEffect.isParticles()), true);
						}
						else
						{
							player.addPotionEffect(new PotionEffect(effect.getKey(), timeLeft, removedEffect.getAmplifier(), removedEffect.isAmbient()), true);
						}
					}
				}
				
				potionEffects_.remove();
			}
		}
	}
	
	public void drinkMilk(Player player)
	{
		this.removedEffects.clear();

		this.check(player, WorldGuardUtils.getCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation()));
	}
	
	public void drinkPotion(Player player, Collection<PotionEffect> effects)
	{
		for(PotionEffect effect : effects)
		{
			this.removedEffects.put(effect.getType(), new PotionEffectDetails(System.nanoTime() + (long)(effect.getDuration() / 20D * TimeUnit.SECONDS.toNanos(1L)), effect.getAmplifier(), effect.isAmbient(), SupportedFeatures.isPotionEffectParticles() ? effect.hasParticles() : true));
		}
		
		this.check(player, WorldGuardUtils.getCommunicator().getRegionContainer().createQuery().getApplicableRegions(player.getLocation()));
	}
}
