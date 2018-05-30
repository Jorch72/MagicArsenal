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

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.DamageHelper;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;
import com.google.common.base.Predicates;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.MinecraftForge;

public class DisruptionSpell implements ISpellEffect {
	public static final int RANGE = 20;
	TargetData.Single<EntityLivingBase> target;
	private int ticksExisted = 0;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		target = TargetData.Single.living(caster);
		if (res.getGlobalCooldown()>0) return;
		
		target.targetRaycast(20);
		if (!target.hasTarget()) return;
		
		SpellEvent event = new SpellEvent.CastOnEntity("disruption", target, EnumElement.ARCANE, EnumElement.FIRE)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.disruption.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			target.clearTarget();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.disruption.cooldown);
			
			SpellEffect.spawnEmitter("spellGather", caster, caster);
		} else {
			this.ticksExisted = -1;
			target.clearTarget();
		}
	}

	@Override
	public int tick() {
		if (ticksExisted==-1 || !target.hasTarget()) {
			return 0;
		}
		
		if (ticksExisted==0) {
			//This is our first tick. Schedule the blast beam for 3 seconds in.
			ticksExisted = 1;
			return 60;
		} else if (ticksExisted>=1 && ticksExisted<7) {
			//Every half second, for three more seconds (six ticks in all), we raycast and annihilate
			RayTraceResult trace = target.raycastToExistingTarget(RANGE, Predicates.alwaysTrue());
			
			if (trace.entityHit!=null) {
				new SpawnParticleEmitterMessage("disruption")
					.from(target.caster)
					.at(trace.entityHit)
					.sendToAllWatchingTarget();
				
				DamageHelper.fireSpellDamage(
						new SpellDamageSource(target.caster, "disruption", EnumElement.ARCANE, EnumElement.FIRE),
						target.getTarget(),
						ArsenalConfig.get().spells.disruption.potency);
				/*
				SpellEvent.DamageEntity event = new SpellEvent.DamageEntity("disruption", target, EnumElement.ARCANE, EnumElement.FIRE)
						.setDamage(ArsenalConfig.get().spells.disruption.potency);
				MinecraftForge.EVENT_BUS.post(event);
				if (!event.isCanceled() && trace.entityHit instanceof EntityLivingBase) {
					EntityLivingBase toHurt = (EntityLivingBase)trace.entityHit;
					toHurt.attackEntityFrom(new SpellDamageSource(target.caster, "disruption", EnumElement.ARCANE, EnumElement.FIRE), event.getDamage());
				}*/
			} else {
				BlockPos targetLoc = trace.getBlockPos();
				if (targetLoc==null) targetLoc = new BlockPos(trace.hitVec);
				
				new SpawnParticleEmitterMessage("disruption")
					.from(target.caster)
					.at(target.caster.getEntityWorld(), targetLoc)
					.sendToAllWatchingTarget();
				
			}
			
			ticksExisted++;
			return 10;
		} else {
			return 0;
		}
	}

}
