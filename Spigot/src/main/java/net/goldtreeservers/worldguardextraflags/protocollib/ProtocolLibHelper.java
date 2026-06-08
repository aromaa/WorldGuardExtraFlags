package net.goldtreeservers.worldguardextraflags.protocollib;

import com.comphenix.protocol.ProtocolLibrary;

public class ProtocolLibHelper
{

	public void onEnable()
	{
		ProtocolLibrary.getProtocolManager().addPacketListener(new RemoveEffectPacketListener());
	}
}
