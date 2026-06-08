package net.goldtreeservers.worldguardextraflags.flags.data;

import java.util.concurrent.TimeUnit;

public record PotionEffectDetails(long endTime, int amplifier, boolean ambient, boolean particles) {
	public long getTimeLeft() {
		return (this.endTime - System.nanoTime());
	}

	public int getTimeLeftInTicks() {
		return (int) (this.getTimeLeft() / TimeUnit.MILLISECONDS.toNanos(50L));
	}
}
