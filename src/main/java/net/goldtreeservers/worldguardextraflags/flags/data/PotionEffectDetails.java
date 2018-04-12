package net.goldtreeservers.worldguardextraflags.flags.data;

import java.util.concurrent.TimeUnit;

import org.bukkit.Color;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PotionEffectDetails
{
	@Getter private final long endTime;
	@Getter private final int amplifier;
	@Getter private final boolean ambient;
	@Getter private final boolean particles;
	@Getter private final Color color;
	
	public double getTimeLeft()
	{
		return (this.endTime - System.nanoTime()) / TimeUnit.MICROSECONDS.toNanos(50L);
	}
	
	public int getTimeLeftInTicks()
	{
		return (int)(this.getTimeLeft() / 0.05);
	}
}
