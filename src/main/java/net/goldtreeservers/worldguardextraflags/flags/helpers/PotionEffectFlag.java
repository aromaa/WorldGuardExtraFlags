package net.goldtreeservers.worldguardextraflags.flags.helpers;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

public class PotionEffectFlag extends Flag<PotionEffect>
{
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
				return new PotionEffect(potionEffect, 160, new Integer(splitd[1]));
			}
			else
			{
				throw new InvalidFlagFormat("Unable to find the potion effect!");
			}
		}
		else
		{
			throw new InvalidFlagFormat("Please use format: <effect name> <effect amplifier>");
		}
	}

	@Override
	public PotionEffect unmarshal(Object o)
	{
		String[] splitd = o.toString().split(" ");
		return new PotionEffect(PotionEffectType.getByName(splitd[0]), 100, new Integer(splitd[1]));
	}
}
