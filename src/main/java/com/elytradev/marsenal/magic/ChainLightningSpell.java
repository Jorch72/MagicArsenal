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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.DamageHelper;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.capability.IMagicResources;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.MinecraftForge;

public class ChainLightningSpell implements ISpellEffect {
	public static final float SECONDARY_RANGE = 8;
	public static final int NUM_ROUNDS = 4;
	public static final int ROUND_LENGTH = 20;
	
	TargetData.Multi<EntityLivingBase> targets;
	Set<EntityLivingBase> zapped = new HashSet<>();
	Set<EntityLivingBase> energizedThisTurn  = new HashSet<>();
	Set<EntityLivingBase> zappedThisTurn = new HashSet<>();
	private int remainingRounds = 0;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		targets = TargetData.Multi.living(caster);
		if (res.getGlobalCooldown()>0) return;
		
		targets.targetRaycast(15);
		if (!targets.hasTargets()) {
			return;
		}
		
		SpellEvent event = new SpellEvent.CastOnEntity("chainLightning", targets.getCaster(), targets.getTargets().iterator().next(), EnumElement.HOLY, EnumElement.AIR)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.chainLightning.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.clearTargets();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.chainLightning.cooldown);
			
			EntityLivingBase target = targets.getTargets().iterator().next();
			
			/*
			SpellEvent.DamageEntity damageEvent = new SpellEvent.DamageEntity("chainLightning", targets.getCaster(), target, EnumElement.HOLY, EnumElement.AIR)
					.setDamage(ArsenalConfig.get().spells.chainLightning.potency);
			MinecraftForge.EVENT_BUS.post(damageEvent);
			
			if (!damageEvent.isCanceled()) {
				target.attackEntityFrom(new SpellDamageSource(targets.caster, "chainLightning", EnumElement.HOLY, EnumElement.AIR), damageEvent.getDamage());
			}*/
			
			SpellDamageSource thunderDamage = new SpellDamageSource(targets.caster, "chainLightning", EnumElement.HOLY, EnumElement.AIR).setElectrical();
			DamageHelper.fireSpellDamage(thunderDamage, target, ArsenalConfig.get().spells.chainLightning.potency);
			SpellEffect.spawnEmitter("lightning", targets.caster, target);
			remainingRounds = NUM_ROUNDS;
		}
	}

	@Override
	public int tick() {
		if (!targets.hasTargets()) return 0;
		if (remainingRounds!=NUM_ROUNDS) {
			zappedThisTurn.clear();
			energizedThisTurn.clear();
			energizedThisTurn.addAll(targets.getTargets());
			
			for(EntityLivingBase energized : targets.getTargets()) {
				if (energized.isDead) continue;
				//Find nearby entities to zap
				AxisAlignedBB aoe = energized.getEntityBoundingBox().expand(SECONDARY_RANGE, SECONDARY_RANGE, SECONDARY_RANGE).expand(-SECONDARY_RANGE, -SECONDARY_RANGE, -SECONDARY_RANGE);
				
				List<EntityLivingBase> toZap = energized.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, aoe, (it)->!energizedThisTurn.contains(it) && it.getDistance(energized)<SECONDARY_RANGE);
				
				for(EntityLivingBase zap : toZap) {
					if (zappedThisTurn.contains(zap)) continue;
					if (zap==targets.caster) continue;
					
					zappedThisTurn.add(zap);
					
					SpellDamageSource thunderDamage = new SpellDamageSource(targets.caster, "chainLightning", EnumElement.HOLY, EnumElement.AIR).setElectrical();
					DamageHelper.fireSpellDamage(thunderDamage, zap, ArsenalConfig.get().spells.chainLightning.potency);
					
					SpellEffect.spawnEmitter("lightning", energized, zap);
				}
			}
			
			targets.clearTargets();
			targets.getTargets().addAll(zappedThisTurn);
		}
		
		remainingRounds--;
		return (remainingRounds>0 && targets.hasTargets()) ? ROUND_LENGTH : 0;
	}

}
