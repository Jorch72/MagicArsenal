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
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.MinecraftForge;

public class HealingWaveSpell implements ISpellEffect {
	private TargetData.Single<EntityLivingBase> targets;
	private static Potion POTION_POISON = Potion.getPotionFromResourceLocation("minecraft:poison");
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		targets = TargetData.Single.living(caster);
		if (targets.targetRaycast(20, TargetData.NON_HOSTILE)==null) return;
		
		SpellEvent event = new SpellEvent.CastOnEntity("healingWave", targets, EnumElement.NATURE, EnumElement.HOLY)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.healingWave.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.clearTarget();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.healingWave.cooldown);
		} else {
			this.targets.clearTarget();
		}
	}

	@Override
	public int tick() {
		if (targets==null || targets.getTarget()==null) return 0;
		
		SpellEffect.spawnEmitter("infuseLife", targets);
		if (targets.getTarget().world.isRemote) return 0;
		if (targets.getTarget().isPotionActive(POTION_POISON)) {
			//Regular poison - cancel out the poison instead of healing.
			targets.getTarget().removeActivePotionEffect(POTION_POISON);
			return 0;
		} else {
			//If there's a Strong poison like wolfsbane or nightshade on the entity, leave both effects and let them fight
			targets.getTarget().addPotionEffect(new PotionEffect(ArsenalItems.POTION_INFUSELIFE, ArsenalConfig.get().spells.healingWave.duration, ArsenalConfig.get().spells.healingWave.amplifier));
			
			return 0;
		}
	}
}
