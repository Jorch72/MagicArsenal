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

package com.elytradev.marsenal.entity;

import java.util.Random;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.client.ParticleVelocity;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.SpellDamageSource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFrostShard extends EntityThrowable {
	private static final float WIDTH = 1.0f;
	private static final float HEIGHT = 0.5f;
	
	private Random random = new Random();
	
	private int maxAge = 40;
	private int age = 0;
	
	public EntityFrostShard(World world) {
		super(world);
		//if (world.isRemote) MagicArsenal.LOG.info("Clientside Frost Shard!");
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}
	
	public EntityFrostShard(World world, EntityLivingBase thrower) {
		super(world, thrower);
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
            if (result.entityHit != null && this.thrower!=null) {
            	if (result.entityHit instanceof EntityLivingBase) {
            		SpellEvent.DamageEntity event = new SpellEvent.DamageEntity("frostShards", this.thrower, (EntityLivingBase)result.entityHit, EnumElement.ARCANE, EnumElement.FROST)
            				.setDamage(ArsenalConfig.get().spells.frostShards.potency);
            		
            		if (!event.isCanceled()) {
            			result.entityHit.attackEntityFrom(new SpellDamageSource(this.thrower, "frostShards", EnumElement.ARCANE, EnumElement.FROST), event.getDamage());
            		}
            	}
            }

            this.setDead();
        }
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if (this.world!=null && this.world.isRemote) {
			spawnParticles();
		}
		
		this.age++;
		if (this.age>maxAge) this.setDead();
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles() {
		if (Minecraft.getMinecraft().gameSettings.particleSetting!=0) return;
		for(int i=0; i<3; i++) {
			float px = (float)(posX + random.nextGaussian()*0.5d);
			float py = (float)(posY);
			float pz = (float)(posZ + random.nextGaussian()*0.5d);
			
			Particle particle = new ParticleVelocity(world,
					px, py, pz,
					0f, -0.6f, 0f
					);
			particle.setParticleTextureIndex(5); //Midway through redstone
			particle.setRBGColorF(0.6f, 0.6f, 0.9667f);
			
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
	}
}
