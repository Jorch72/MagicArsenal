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
import com.elytradev.marsenal.item.ArsenalItems;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;

public class StunSpell extends SpellEffect {
	private TargetData.Single<EntityLivingBase> targets;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		targets = TargetData.Single.living(caster);
		if (targets.targetRaycast(20, (EntityLivingBase entity)->true)==null) return;
		
		SpellEvent event = new SpellEvent.CastOnEntity("stun", targets, EnumElement.FROST, EnumElement.UNDEATH)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.stun.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.clearTarget();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.stun.cooldown);
		} else {
			this.targets.clearTarget();
		}
	}

	@Override
	public int tick() {
		if (targets==null || targets.getTarget()==null) return 0;
		
		//SpellEffect.spawnEmitter("stun", targets); //TODO: Implement
		if (targets.getTarget().world.isRemote) return 0;
		targets.getTarget().addPotionEffect(new PotionEffect(ArsenalItems.POTION_STUN, ArsenalConfig.get().spells.stun.duration, ArsenalConfig.get().spells.stun.amplifier));
		
		return 0;
	}
}
