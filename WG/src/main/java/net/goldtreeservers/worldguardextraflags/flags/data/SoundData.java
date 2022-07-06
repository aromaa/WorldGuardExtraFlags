package net.goldtreeservers.worldguardextraflags.flags.data;

import org.bukkit.SoundCategory;

public record SoundData(String sound, int interval, SoundCategory source, float volume, float pitch)
{
}
