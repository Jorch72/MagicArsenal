/*
 * MIT License
 *
 * Copyright (c) 2018 Isaac Ellingson (Falkreon) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.elytradev.marsenal.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.client.ParticleEmitters;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@ReceivedOn(Side.CLIENT)
public class SpawnParticleEmitterMessage extends Message {
	private String key;
	@MarshalledAs("int32")
	private int worldId = 0;
	@MarshalledAs("f32")
	private float x = 0;
	@MarshalledAs("f32")
	private float y = 0;
	@MarshalledAs("f32")
	private float z = 0;
	@MarshalledAs("int32")
	private int entityId = -1;
	
	public SpawnParticleEmitterMessage() {
		super(MagicArsenal.CONTEXT);
	}
	
	public SpawnParticleEmitterMessage(String key) {
		super(MagicArsenal.CONTEXT);
		this.key = key;
	}

	@Override
	protected void handle(EntityPlayer player) {
		if (player.getEntityWorld().provider.getDimension()!=worldId) return; //No sense spawning fx for worlds we're not in
		Entity entity = null;
		if (entityId!=-1) entity = player.getEntityWorld().getEntityByID(entityId);
		ParticleEmitters.spawn(player.getEntityWorld(), x, y, z, entity, key);
	}

	public SpawnParticleEmitterMessage at(Entity entity) {
		worldId = entity.getEntityWorld().provider.getDimension();
		entityId = entity.getEntityId();
		x = (float)entity.posX;
		y = (float)entity.posY+2f;
		z = (float)entity.posZ;
		
		return this;
	}
	
	public SpawnParticleEmitterMessage atLocationOf(Entity entity) {
		worldId = entity.getEntityWorld().provider.getDimension();
		x = (float)entity.posX;
		y = (float)entity.posY+2f;
		z = (float)entity.posZ;
		
		return this;
	}

}
