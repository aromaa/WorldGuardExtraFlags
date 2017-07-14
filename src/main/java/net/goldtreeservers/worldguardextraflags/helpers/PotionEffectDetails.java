package net.goldtreeservers.worldguardextraflags.helpers;

import org.bukkit.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.goldtreeservers.worldguardextraflags.utils.TimeUtils;

@RequiredArgsConstructor
public class PotionEffectDetails
{
	@Getter private final double endTime;
	@Getter private final int amplifier;
	@Getter private final boolean ambient;
	@Getter private final boolean particles;
	@Getter private final Color color;
	
	public double getTimeLeft()
	{
		return this.endTime - TimeUtils.getUnixtimestamp();
	}
	
	public int getTimeLeftInTicks()
	{
		return (int)(this.getTimeLeft() / 0.05);
	}
}
