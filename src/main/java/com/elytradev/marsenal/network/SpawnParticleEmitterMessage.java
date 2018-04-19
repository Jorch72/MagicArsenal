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
import com.elytradev.marsenal.magic.TargetData;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	@MarshalledAs("int32")
	private int sourceId = -1;
	
	private transient Entity target;
	private transient World targetWorld;
	private transient BlockPos targetLoc;
	
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
		Entity source = null;
		if (sourceId!=-1) {
			source = player.getEntityWorld().getEntityByID(sourceId);
			if (source.isDead) source = null;
		}
		
		Entity target = null;
		if (entityId!=-1) {
			target = player.getEntityWorld().getEntityByID(entityId);
			if (target.isDead) target = null;
		}
		ParticleEmitters.spawn(player.getEntityWorld(), x, y, z, source, target, key);
	}

	public SpawnParticleEmitterMessage from(Entity entity) {
		if (entity!=null) sourceId = entity.getEntityId();
		return this;
	}
	
	public SpawnParticleEmitterMessage at(Entity entity) {
		target = entity;
		if (entity!=null) {
			worldId = entity.getEntityWorld().provider.getDimension();
			entityId = entity.getEntityId();
			x = (float)entity.posX;
			y = (float)entity.posY-(entity.height/2);
			z = (float)entity.posZ;
		}
		return this;
	}
	
	public SpawnParticleEmitterMessage at(World world, BlockPos pos) {
		targetWorld = world;
		targetLoc = pos;
		worldId = world.provider.getDimension();
		x = pos.getX()+0.5f;
		y = pos.getY()+0.5f;
		z = pos.getZ()+0.5f;
		
		return this;
	}
	
	public SpawnParticleEmitterMessage atLocationOf(Entity entity) {
		targetWorld = entity.getEntityWorld();
		targetLoc = entity.getPosition();
		worldId = entity.getEntityWorld().provider.getDimension();
		x = (float)entity.posX;
		y = (float)entity.posY+2f;
		z = (float)entity.posZ;
		
		return this;
	}
	
	public SpawnParticleEmitterMessage with(TargetData.Single<? extends Entity> targetData) {
		if (targetData.getCaster()!=null) sourceId = targetData.getCaster().getEntityId();
		if (targetData.getTarget()!=null) {
			this.target = targetData.getTarget();
			worldId = targetData.getTarget().getEntityWorld().provider.getDimension();
			entityId = targetData.getTarget().getEntityId();
			x = (float)targetData.getTarget().posX;
			y = (float)targetData.getTarget().posY-(targetData.getTarget().height/2);
			z = (float)targetData.getTarget().posZ;
		}
		
		return this;
	}
	
	public SpawnParticleEmitterMessage withReversed(TargetData.Single<? extends Entity> targetData) {
		if (targetData.getTarget()!=null) sourceId = targetData.getTarget().getEntityId();
		if (targetData.getCaster()!=null) {
			this.target = targetData.getCaster();
			worldId = targetData.getCaster().getEntityWorld().provider.getDimension();
			entityId = targetData.getCaster().getEntityId();
			x = (float)targetData.getCaster().posX;
			y = (float)targetData.getCaster().posY-(targetData.getCaster().height/2);
			z = (float)targetData.getCaster().posZ;
		}
		
		return this;
	}

	public void sendToAllWatchingTarget() {
		if (target!=null) {
			this.sendToAllWatchingAndSelf(target);
		} else if (targetWorld!=null && targetLoc!=null) {
			this.sendToAllWatching(targetWorld, targetLoc);
		}
	}
}
