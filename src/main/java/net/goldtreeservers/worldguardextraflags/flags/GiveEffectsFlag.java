package net.goldtreeservers.worldguardextraflags.flags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

public class GiveEffectsFlag extends Handler
{
	public static final Factory FACTORY = new Factory();
    public static class Factory extends Handler.Factory<GiveEffectsFlag>
    {
        @Override
        public GiveEffectsFlag create(Session session)
        {
            return new GiveEffectsFlag(session);
        }
    }
    
    private HashMap<String, PotionEffect> originalPotionEffects = new HashMap<String, PotionEffect>();
    private HashSet<PotionEffectType> effectsGiven = new HashSet<PotionEffectType>();
    
	protected GiveEffectsFlag(Session session)
	{
		super(session);
	}
	
	@Override
	public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType)
	{
		for(ProtectedRegion enterd : entered)
		{
			Set<PotionEffect> potionEffects = enterd.getFlag(WorldGuardExtraFlagsPlugin.giveEffects);
			if (potionEffects != null)
			{
				for (PotionEffect potionEffect : potionEffects)
				{
					if (potionEffect != null)
					{
						if (player.hasPotionEffect(potionEffect.getType()))
						{
							if (!this.originalPotionEffects.containsKey(potionEffect.getType().getName()))
							{
								for(PotionEffect currentPotionEffect : player.getActivePotionEffects())
								{
									if (currentPotionEffect.getType().getName().equals(potionEffect.getType().getName()))
									{
										this.originalPotionEffects.put(potionEffect.getType().getName(), currentPotionEffect);
										break;
									}
								}
							}
							
							player.removePotionEffect(potionEffect.getType());
						}
						
						this.effectsGiven.add(potionEffect.getType());
						player.addPotionEffect(potionEffect);
					}
				}
			}
		}
		
		for(ProtectedRegion exitd : exited)
		{
			Set<PotionEffect> potionEffects = exitd.getFlag(WorldGuardExtraFlagsPlugin.giveEffects);
			if (potionEffects != null)
			{
				for(PotionEffect potionEffect : potionEffects)
				{
					if (potionEffect != null)
					{
						this.effectsGiven.remove(potionEffect.getType());
						player.removePotionEffect(potionEffect.getType());
						
						PotionEffect oldPotionEffect = this.originalPotionEffects.remove(potionEffect.getType().getName());
						if (oldPotionEffect != null)
						{
							player.addPotionEffect(oldPotionEffect);
						}
					}
				}
			}
		}
		return true;
	}
	
	@Override
	public void tick(Player player, ApplicableRegionSet set)
	{
		if (this.originalPotionEffects != null)
		{
			for(PotionEffect oldPotionEffect : this.originalPotionEffects.values().toArray(new PotionEffect[0]))
			{
				this.originalPotionEffects.remove(oldPotionEffect);
				this.originalPotionEffects.put(oldPotionEffect.getType().getName(), new PotionEffect(oldPotionEffect.getType(), oldPotionEffect.getDuration() - 20, oldPotionEffect.getAmplifier(), oldPotionEffect.isAmbient(), oldPotionEffect.hasParticles()));
			}
		}
		
		List<PotionEffectType> shouldRemove = new ArrayList<PotionEffectType>(this.effectsGiven);
		for(Set<PotionEffect> potionEffects : set.queryAllValues(WorldGuardExtraFlagsPlugin.getWorldGuard().wrapPlayer(player), WorldGuardExtraFlagsPlugin.giveEffects))
		{
			if (potionEffects != null)
			{
				for(PotionEffect potionEffect : potionEffects)
				{
					if (potionEffect != null)
					{
						if (player.hasPotionEffect(potionEffect.getType()))
						{
							for(PotionEffect effect : player.getActivePotionEffects())
							{
								if (effect.getType().equals(potionEffect.getType()))
								{
									if (effect.getAmplifier() != potionEffect.getAmplifier())
									{
										player.removePotionEffect(potionEffect.getType());
									}
								}
							}
							
							player.addPotionEffect(potionEffect);
						}
						else
						{
							player.addPotionEffect(potionEffect);
						}
						
						this.effectsGiven.add(potionEffect.getType());
						shouldRemove.remove(potionEffect.getType());
					}
				}
			}
		}
		
		for(PotionEffectType effectType : shouldRemove)
		{
			PotionEffect oldPotionEffect = this.originalPotionEffects.remove(effectType.getName());
			if (oldPotionEffect != null)
			{
				if (player.hasPotionEffect(effectType))
				{
					player.removePotionEffect(effectType);
				}
				
				player.addPotionEffect(oldPotionEffect);
			}
		}
	}
	
	public void drinkMilk()
	{
		this.originalPotionEffects.clear();
	}
	
	public void drinkPotion(Collection<PotionEffect> effects)
	{
		for(PotionEffect potionEffect : effects)
		{
			this.originalPotionEffects.put(potionEffect.getType().getName(), potionEffect);
		}
	}
}
