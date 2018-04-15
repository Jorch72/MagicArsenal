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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.common.MinecraftForge;

public class OblationSpell implements ISpellEffect {
	private TargetData targets;
	private int ticksRemaining;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		targets = new TargetData(caster);
		targets.targetEntity(20);
		if (targets.targets.isEmpty()) return;
		Entity target = targets.targets.get(0);
		if (target instanceof EntityMob || !(target instanceof EntityLivingBase)) {
			targets.targets.clear();
			return;
		}
		
		SpellEvent event = new SpellEvent.CastOnEntity("oblation", targets.caster, targets.targets.get(0), EnumElement.CHAOS, EnumElement.NATURE);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.getTargets().clear();
			ticksRemaining = 0;
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, ArsenalConfig.get().spells.oblation.cost)) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.oblation.cooldown);
			
			this.ticksRemaining = 5;
		} else {
			//activation failure
		}
	}

	@Override
	public int tick() {
		if (targets==null || targets.targets.isEmpty()) return 0;
		
		targets.caster.attackEntityFrom(
				new SpellDamageSource(targets.caster, "drain_life", EnumElement.CHAOS,  EnumElement.NATURE).setDamageIsAbsolute(),
				ArsenalConfig.get().spells.oblation.potency);
		
		for(Entity entity : targets.targets) {
			if (entity instanceof EntityLivingBase) {
				((EntityLivingBase)entity).heal(ArsenalConfig.get().spells.oblation.potency);
				new SpawnParticleEmitterMessage("drainLife").at(targets.caster).from(entity).sendToAllWatchingAndSelf(targets.caster);
				new SpawnParticleEmitterMessage("infuseLife").at(entity).from(targets.caster).sendToAllWatchingAndSelf(entity);
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) {
			return 0;
		} else {
			return 10;
		}
	}
}
