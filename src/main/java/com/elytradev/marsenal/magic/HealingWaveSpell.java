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

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;

public class HealingWaveSpell implements ISpellEffect {
	private TargetData.Single<EntityLivingBase> targets;
	private int ticksRemaining;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		targets = TargetData.Single.living(caster);
		if (targets.targetRaycast(20, TargetData.NON_HOSTILE)==null) return;
		
		SpellEvent event = new SpellEvent.CastOnEntity("healingWave", targets, EnumElement.NATURE, EnumElement.HOLY);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.clearTarget();
			ticksRemaining = 0;
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, ArsenalConfig.get().spells.healingWave.cost)) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.healingWave.cooldown);
			
			this.ticksRemaining = 5;
		} else {
			//activation failure
			this.ticksRemaining = 0;
			this.targets.clearTarget();
		}
	}

	@Override
	public int tick() {
		if (targets==null || targets.getTarget()==null) return 0;

		targets.getTarget().heal(ArsenalConfig.get().spells.healingWave.potency);
		SpellEffect.spawnEmitter("infuseLife", targets);
		
		ticksRemaining--;
		if (ticksRemaining<=0) {
			return 0;
		} else {
			return 20;
		}
	}
}
