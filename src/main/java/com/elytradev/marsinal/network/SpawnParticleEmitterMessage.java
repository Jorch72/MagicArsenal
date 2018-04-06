package com.elytradev.marsinal.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.marsinal.MagicArsenal;

import net.minecraft.entity.player.EntityPlayer;

public class SpawnParticleEmitterMessage extends Message {
	private int id;
	private int entityId;
	
	public SpawnParticleEmitterMessage() {
		super(MagicArsenal.CONTEXT);
	}

	@Override
	protected void handle(EntityPlayer player) {
		
	}

}
