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

package com.elytradev.marsenal.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;

public class PotionStun extends PotionBase {
	
	public PotionStun() {
		super(true, 0xFFd28500);
		
		setTexture("magicarsenal:textures/effects/stun.png");
		setPotionName("effect.magicarsenal.stun");
		setRegistryName("magicarsenal", "stun");
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		if (!entity.isNonBoss()) return; //Bosses are immune to cc
		
		if (entity.getEntityWorld().isRemote) {
			if (entity instanceof EntityPlayer) {
				entity.motionX = 0;
				entity.motionZ = 0;
			} else {
				entity.rotationYaw = (float) (Math.random()*180 - 90);
				entity.rotationPitch = (float) (Math.random()*180 - 90);
			}
			
			return;
		}
		
		//String id = EntityList.getEntityString(entity);
		if (entity instanceof EntityPlayer) {
			//Stun needs to affect players differently from mobs.
		} else {
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;
			entity.rotationYaw = (float) (Math.random()*180 - 90);
			entity.rotationPitch = (float) (Math.random()*180 - 90);
		}
		
		//TODO: Particles - maybe spawn a short-lived emitter instead?
		for(int i=0; i<3; i++) {
		entity.getEntityWorld().spawnParticle(EnumParticleTypes.CRIT_MAGIC,
				entity.posX - 0.5 + Math.random(), entity.posY + (entity.height/2) + Math.random(), entity.posZ - 0.5 + Math.random(),
				Math.random()*0.2 - 0.1, 0.1, Math.random()*0.2 - 0.1);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
		/*
		int ticks = 12 >> (amplifier + 1);
		
		if (ticks > 0) {
			return duration % ticks == 0;
		} else {
			return true;
		}*/
	}
	
	private static class AITaskBeStunned extends EntityAIBase {

		@Override
		public boolean shouldExecute() {
			return true;
		}
		
		@Override
		public boolean shouldContinueExecuting() {
			
			return true;
		}
		
	}
}
