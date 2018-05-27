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

package com.elytradev.marsenal.magic;

import com.elytradev.marsenal.item.ArsenalItems;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.event.entity.living.LivingEvent;

/** Misc event handlers for things that can't be done in the regular flow of code */
public class SpellHandlers {
	
	public static void onUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		if (entity.isNonBoss() && entity.isPotionActive(ArsenalItems.POTION_STUN)) {
			
			if (entity instanceof EntityMob) {
				//Additionally disrupt as much AI as possible without manually going in and yanking AITasks from the table
				EntityMob mob = (EntityMob)entity;
				mob.setAttackTarget(null);
				
				if (entity instanceof EntityCreeper) {
					((EntityCreeper)entity).setCreeperState(-1); //Defuse if possible
				}
			} else {
				entity.setLastAttackedEntity(null);
			}
			
			if (entity.getEntityWorld().isRemote) {
				//The below causes chickens to flap their wings for some reason. I like it.
				entity.rotationPitch = (float) (Math.random()*180f) - 90f;
				entity.prevCameraPitch = entity.rotationPitch;
				entity.rotationYaw = (float) (Math.random()*360f);
				entity.prevRotationPitch = entity.rotationPitch;
				entity.prevRotationYaw = entity.rotationYaw;
				entity.rotationYawHead = (float) (Math.random()*180f) - 90f;
				entity.prevRotationYawHead = entity.rotationYawHead;
			}
		}
	}
}
