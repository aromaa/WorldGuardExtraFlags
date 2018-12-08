package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class PotionEffectFlag extends Flag<PotionEffect>
{
	//This is in ticks
	//So 20 * 15 gives us 15s of the potion effect
	//This avoid the effect running out indication
	//Also we add extra 19 ticks (almost a second) to avoid the timer constantly going from 15s to 14s and back (Its annoying)
	private static final int POTION_EFFECT_DURATION = 20 * 15 + 19;
	
	public PotionEffectFlag(String name)
	{
		super(name);
	}

	@Override
	public Object marshal(PotionEffect o)
	{
		return o.getType().getName() + " " + o.getAmplifier();
	}

	@Override
	public PotionEffect parseInput(FlagContext context) throws InvalidFlagFormat
	{
		String[] splitd = context.getUserInput().trim().split(" ");
		if (splitd.length == 2)
		{
			PotionEffectType potionEffect = PotionEffectType.getByName(splitd[0]);
			if (potionEffect != null)
			{
				return new PotionEffect(potionEffect, 319, new Integer(splitd[1]));
			}
			else
			{
				throw new InvalidFlagFormat("Unable to find the potion effect type! Please refer to https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html");
			}
		}
		else
		{
			throw new InvalidFlagFormat("Please use the following format: <effect name> <effect amplifier>");
		}
	}

	@Override
	public PotionEffect unmarshal(Object o)
	{
		String[] splitd = o.toString().split(" ");
		
		PotionEffectType type = PotionEffectType.getByName(splitd[0]);
		int amplifier = Integer.parseInt(splitd[1]);
		
		return new PotionEffect(type, PotionEffectFlag.POTION_EFFECT_DURATION, amplifier);
	}
}
