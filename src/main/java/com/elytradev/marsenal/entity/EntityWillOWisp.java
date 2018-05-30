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
import com.elytradev.marsenal.DamageHelper;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.client.ParticleVelocity;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.SpellDamageSource;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityWillOWisp extends EntityThrowable {
	private static final float WIDTH = 0.8f;
	private static final float HEIGHT = 0.8f;
	
	private Random random = new Random();
	
	private int maxAge = 320;
	private int age = 0;
	
	public EntityWillOWisp(World world) {
		super(world);
		//if (world.isRemote) MagicArsenal.LOG.info("Clientside Will O Wisp!");
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}
	
	public EntityWillOWisp(World world, EntityLivingBase thrower) {
		super(world, thrower);
		
		this.width = WIDTH;
		this.height = HEIGHT;
		this.setNoGravity(true);
	}

	@Override
	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit != null && this.thrower!=null) {
				if (result.entityHit instanceof EntityLivingBase && result.entityHit!=thrower) {
					
					DamageHelper.fireSpellDamage( new SpellDamageSource(this.thrower, "willOWisp", EnumElement.UNDEATH, EnumElement.FIRE),
							(EntityLivingBase) result.entityHit, ArsenalConfig.get().spells.willOWisp.potency);
					
					result.entityHit.setFire(3);
					
					/*
					 * SpellEvent.DamageEntity event = new SpellEvent.DamageEntity("willOWisp",
					 * this.thrower, (EntityLivingBase)result.entityHit, EnumElement.UNDEATH,
					 * EnumElement.FIRE) .setDamage(ArsenalConfig.get().spells.willOWisp.potency);
					 * 
					 * if (!event.isCanceled()) { result.entityHit.attackEntityFrom(new
					 * SpellDamageSource(this.thrower, "willOWisp", EnumElement.UNDEATH,
					 * EnumElement.FIRE), event.getDamage());
					 * 
					 * result.entityHit.setFire(2+random.nextInt(5)); //2..6 seconds }
					 */
				}
			} else {
				Vec3i vec = result.sideHit.getDirectionVec();
				if (vec.getX() != 0)
					motionX *= -vec.getX();
				if (vec.getY() != 0)
					motionY *= -vec.getY();
				if (vec.getZ() != 0)
					motionZ *= -vec.getZ();
			}
		}
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (world==null) return;
		
		if (world.isRemote) {
			spawnParticles();
		} else {
			this.age++;
			if (this.age>maxAge) this.setDead();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles() {
		if (Minecraft.getMinecraft().gameSettings.particleSetting!=0) return;
		for(int i=0; i<3; i++) {
			float px = (float)(posX + random.nextGaussian()*0.2d);
			float py = (float)(posY);
			float pz = (float)(posZ + random.nextGaussian()*0.2d);
			
			Particle particle = new ParticleVelocity(world,
					px, py+0.5f, pz,
					0f, (float)random.nextGaussian()*0.1f, 0f
					);
			particle.setParticleTextureIndex(48); //flame
			particle.multipleParticleScaleBy(1.5f);
			particle.setRBGColorF(0.6f, 0.6f, 0.9667f);
			
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
	}
}
