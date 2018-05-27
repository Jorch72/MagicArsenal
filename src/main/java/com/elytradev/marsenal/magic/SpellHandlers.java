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
