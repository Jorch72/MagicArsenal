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

/**
 * Abstract super for instant, single-target spells
 */
public abstract class InstantSingleTargetSpell extends SpellEffect {
	protected TargetData.Single<EntityLivingBase> target;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		target = TargetData.Single.living(caster);
		if (res.getGlobalCooldown()>0) return;
	
		target.targetRaycast(getRange());
		
		if (!target.hasTarget()) return;
		
		SpellEvent event = new SpellEvent.CastOnEntity(getSpellKey(), target, getElements())
				.withCost(IMagicResources.RESOURCE_STAMINA, getConfig().cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			target.clearTarget();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, getConfig().cooldown);
			
			SpellEffect.spawnEmitter(getEmitterKey(), target);
			
			onActivate(caster, res);
		} else {
			target.clearTarget();
			return;
		}
	}
	
	@Override
	public int tick() {
		return 0;
	}
	
	public abstract String getSpellKey();
	public abstract String getEmitterKey();
	public abstract int getRange();
	public abstract ArsenalConfig.SpellEntry getConfig();
	public abstract void onActivate(EntityLivingBase caster, IMagicResources res);
	public abstract EnumElement[] getElements();
}
