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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;

public class MagmaBlastSpell implements ISpellEffect {
	EntityLivingBase caster;
	
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		this.caster = caster;
		SpellEvent event = new SpellEvent
				.CastOnArea("magmaBlast", caster.getEntityWorld(), caster.getPosition(), caster.getPosition(), 5, EnumElement.ARCANE, EnumElement.FIRE)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.magmaBlast.cost);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			this.caster = null;
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.magmaBlast.cooldown);
		} else {
			this.caster = null;
		}
		
		
	}

	@Override
	public int tick() {
		if (caster==null) return 0;
		
		Vec3d epicenter = caster.getPositionEyes(1.0f).add(caster.getLookVec().normalize().scale(2.0));
		
		//TODO: Spawn FX
		//caster.getEntityWorld().spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, epicenter.x, epicenter.y, epicenter.z, 0, 0, 0, 0); //Doesn't make it to clients
		
		
		AxisAlignedBB aoe = new AxisAlignedBB(epicenter.x-2.5f, epicenter.y-2.5f, epicenter.z-2.5f, epicenter.x+2.5f, epicenter.y+2.5f, epicenter.z+2.5f);
		List<Entity> targets = caster.getEntityWorld().getEntitiesWithinAABBExcludingEntity(caster, aoe);
		for(Entity target : targets) {
			if (target instanceof EntityLivingBase) {
				
				SpellEvent.DamageEntity event = new SpellEvent
						.DamageEntity("magmaBlast", caster, (EntityLivingBase)target, EnumElement.ARCANE, EnumElement.FIRE)
						.setDamage(ArsenalConfig.get().spells.magmaBlast.potency);
				MinecraftForge.EVENT_BUS.post(event);
				
				if (!event.isCanceled()) {
					((EntityLivingBase)target).attackEntityFrom(new SpellDamageSource(caster, "magmaBlast", EnumElement.ARCANE, EnumElement.FIRE), event.getDamage());
				}
			}
			
			Vec3d vec = new Vec3d(target.posX, target.posY+(target.height/2), target.posZ).subtract(epicenter).normalize().scale(1.5f);
			
			target.addVelocity(vec.x, vec.y, vec.z);
		}
		
		return 0;
	}
	
}
