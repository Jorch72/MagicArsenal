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
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

public class DrainLifeSpell implements ISpellEffect {
	TargetData.Single<EntityLivingBase> target;
	private int counter = 5;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		target = TargetData.Single.living(caster);
		
		if (res.getGlobalCooldown()<=0) {
			//Find a victim
			target.targetRaycast(8);
			if (target.getTarget()==null) return;
			
			SpellEvent.CastOnEntity event = new SpellEvent.CastOnEntity("drainLife", target, EnumElement.UNDEATH, EnumElement.NATURE)
					.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.drainLife.cost);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled()) {
				target.clearTarget();
				return;
			}
			
			if (SpellEffect.activateWithStamina(target.getCaster(), event.getCost())) {
				res.setGlobalCooldown(ArsenalConfig.get().spells.drainLife.cooldown);
			} else {
				target.clearTarget();
			}
		} else {
			//GCD isn't ready. Do nothing
		}
	}

	@Override
	public int tick() {
		if (target.getTarget()==null) return 0;
		
		SpellEvent.DamageEntity event = new SpellEvent.DamageEntity("drainLife", target, EnumElement.UNDEATH, EnumElement.NATURE)
				.setDamage(ArsenalConfig.get().spells.drainLife.potency);
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isCanceled()) {
			SpellEffect.spawnEmitter("drainLife", target);
			
			boolean success = target.getTarget().attackEntityFrom(new SpellDamageSource(target.caster, "drain_life", EnumElement.UNDEATH,  EnumElement.NATURE), event.getDamage());
			if (success) {
				new SpawnParticleEmitterMessage("infuseLife").from(target.caster).at(target.caster).sendToAllWatchingTarget();
				target.caster.heal(ArsenalConfig.get().spells.drainLife.potency/2f);
			}
		}

		counter--;
		return (counter<=0) ? 0 : 20*2;
	}
}
