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

import com.elytradev.marsenal.ArsenalConfig;
import com.elytradev.marsenal.SpellEvent;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class HealingCircleSpell implements ISpellEffect {
	public static final int RADIUS = 5;
	
	private int ticksRemaining;
	private World world;
	private BlockPos epicenter;
	private AxisAlignedBB aoe;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		world = caster.getEntityWorld();
		epicenter = caster.getPosition();
		
		SpellEvent event = new SpellEvent.CastOnArea("healingCircle", caster, caster.getPosition(), RADIUS, EnumElement.HOLY, EnumElement.AIR)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.healingCircle.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			ticksRemaining = 0;
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.healingCircle.cooldown);
			
			this.ticksRemaining = 10;
			
			new SpawnParticleEmitterMessage("healingSphere").at(world, epicenter).sendToAllAround(world, caster, 16*7);
		} else {
			ticksRemaining = 0;
			//activation failure
		}
	}

	@Override
	public int tick() {
		if (ticksRemaining<=0) return 0;
		
		if (aoe==null) aoe = new AxisAlignedBB(
				epicenter.getX()-RADIUS,
				epicenter.getY()-RADIUS,
				epicenter.getZ()-RADIUS,
				epicenter.getX()+RADIUS,
				epicenter.getY()+RADIUS,
				epicenter.getZ()+RADIUS);
		
		List<EntityLivingBase> withinCircle = world.getEntitiesWithinAABB(
				EntityLivingBase.class,
				aoe,
				(it)->it.getDistanceSq(epicenter.getX(), epicenter.getY(), epicenter.getZ()) < RADIUS*RADIUS
				);
		
		for(EntityLivingBase target: withinCircle) {
			if (TargetData.NON_HOSTILE.test(target)) { //We can't go healing zombies, can we?
				target.heal(ArsenalConfig.get().spells.healingCircle.potency);
			}
		}
		
		ticksRemaining--;
		if (ticksRemaining<=0) {
			return 0;
		} else {
			return 40;
		}
	}
}
