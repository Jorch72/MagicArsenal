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

import java.util.List;
import java.util.Random;

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HealingCircleSpell implements ISpellEffect {
	public static final int RADIUS = 5;
	
	private TargetData targets;
	private int ticksRemaining;
	private World world;
	private BlockPos epicenter;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		targets = new TargetData(caster);
		world = caster.getEntityWorld();
		epicenter = caster.getPosition();
		
		if (SpellEffect.activateWithStamina(caster, ArsenalConfig.get().spells.healingCircle.cost)) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.healingCircle.cooldown);
			
			this.ticksRemaining = 10;
			
			new SpawnParticleEmitterMessage("healingSphere").at(world, epicenter).sendToAllAround(world, caster, 16*7);
		} else {
			//activation failure
		}
	}

	@Override
	public int tick() {
		//TODO: Cache targets for a couple ticks?
		List<EntityLivingBase> withinCircle = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(
				epicenter.getX()-RADIUS,
				epicenter.getY()-RADIUS,
				epicenter.getZ()-RADIUS,
				epicenter.getX()+RADIUS,
				epicenter.getY()+RADIUS,
				epicenter.getZ()+RADIUS),
				(it)->it.getDistanceSq(epicenter.getX(), epicenter.getY(), epicenter.getZ()) < RADIUS*RADIUS
				);
		
		//EntityLivingBase target = withinCircle.get(rnd.nextInt(withinCircle.size()));
		for(EntityLivingBase target: withinCircle) {
			target.heal(ArsenalConfig.get().spells.healingCircle.potency);
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) {
			return 0;
		} else {
			return 40;
		}
	}
}
