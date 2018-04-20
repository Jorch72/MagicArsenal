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
		
		SpellEvent event = new SpellEvent.CastOnEntity("chainLightning", targets.getCaster(), targets.getTargets().iterator().next(), EnumElement.HOLY, EnumElement.FIRE)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.chainLightning.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			targets.clearTargets();
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.chainLightning.cooldown);
			
			//TODO: Spell
			remainingRounds = NUM_ROUNDS;
		}
	}

	@Override
	public int tick() {
		if (!targets.hasTargets()) return 0;
		
		zappedThisTurn.clear();
		energizedThisTurn.clear();
		energizedThisTurn.addAll(targets.getTargets());
		
		for(EntityLivingBase energized : targets.getTargets()) {
			//Find nearby entities to zap
			AxisAlignedBB aoe = energized.getEntityBoundingBox().expand(SECONDARY_RANGE, SECONDARY_RANGE, SECONDARY_RANGE).expand(-SECONDARY_RANGE, -SECONDARY_RANGE, -SECONDARY_RANGE);
			
			List<EntityLivingBase> toZap = energized.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, aoe, (it)->!energizedThisTurn.contains(it) && it.getDistance(energized)<SECONDARY_RANGE);
			
			for(EntityLivingBase zap : toZap) {
				
				
				
				
			}
			
			
			
		}
		
		targets.clearTargets();
		targets.getTargets().addAll(zappedThisTurn);
		
		remainingRounds--;
		return (remainingRounds>0) ? ROUND_LENGTH : 0;
	}

}
