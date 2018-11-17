package net.goldtreeservers.worldguardextraflags.flags.data;

public class SoundData
{
	private final String sound;
	private final int interval;
	
	public SoundData(String sound, int interval)
	{
		this.sound = sound;
		this.interval = interval;
	}
	
	public String getSound()
	{
		return this.sound;
	}
	
	public int getInterval()
	{
		return this.interval;
	}
}
