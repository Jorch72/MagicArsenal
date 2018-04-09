package com.elytradev.marsenal.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

@ReceivedOn(Side.CLIENT)
public class ConfigMessage extends Message {
	private String payload;
	
	public ConfigMessage() {
		super(MagicArsenal.CONTEXT);
	}
	
	public ConfigMessage(String payload) {
		super(MagicArsenal.CONTEXT);
		this.payload = payload;
	}
	
	public ConfigMessage(ArsenalConfig config) {
		this(config.toString());
	}

	@Override
	protected void handle(EntityPlayer player) {
		ArsenalConfig.resolve(payload);
		MagicArsenal.LOG.info("Received config from server");
	}
}
