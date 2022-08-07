package net.goldtreeservers.worldguardextraflags.flags.helpers;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;

import net.goldtreeservers.worldguardextraflags.flags.data.SoundData;
import org.bukkit.SoundCategory;

import java.util.Locale;

public class SoundDataFlag extends Flag<SoundData>
{
	public SoundDataFlag(String name)
	{
		super(name);
	}

	@Override
	public Object marshal(SoundData o)
	{
		return o.sound() + " " + o.interval() + " " + o.source() + " " + o.volume() + " " + o.pitch();
	}

	@Override
	public SoundData parseInput(FlagContext context) throws InvalidFlagFormat
	{
		String[] splitd = context.getUserInput().trim().split(" ");
		if (splitd.length >= 2 && splitd.length <= 5)
		{
			return this.getSoundData(splitd);
		}
		else
		{
			throw new InvalidFlagFormat("Please use format: <sound name> <interval in ticks> [source] [volume] [pitch]");
		}
	}

	@Override
	public SoundData unmarshal(Object o)
	{
		String[] splitd = o.toString().split(" ");

		return this.getSoundData(splitd);
	}

	private SoundData getSoundData(String[] splitd)
	{
		return new SoundData(
				splitd[0],
				Integer.parseInt(splitd[1]),
				splitd.length >= 3 ? SoundCategory.valueOf(splitd[2].toUpperCase(Locale.ROOT)) : SoundCategory.MASTER,
				splitd.length >= 4 ? Float.parseFloat(splitd[3]) : Float.MAX_VALUE,
				splitd.length >= 5 ? Float.parseFloat(splitd[4]) : 1
		);
	}
}
