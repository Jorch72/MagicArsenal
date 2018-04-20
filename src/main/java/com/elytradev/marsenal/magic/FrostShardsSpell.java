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
import com.elytradev.marsenal.entity.EntityFrostShard;

import net.minecraft.entity.EntityLivingBase;

public class FrostShardsSpell implements ISpellEffect {
	private EntityLivingBase caster;
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
		if (res.getGlobalCooldown()>0) return;
		
		this.caster = caster;
		
		SpellEvent.CastProjectile event = new SpellEvent.CastProjectile("frostShards", caster, EnumElement.ARCANE, EnumElement.FROST)
				.withCost(IMagicResources.RESOURCE_STAMINA, ArsenalConfig.get().spells.frostShards.cost);
		if (event.isCanceled()) {
			this.caster = null;
			return;
		}
		
		if (SpellEffect.activateWithStamina(caster, event.getCost())) {
			SpellEffect.activateCooldown(caster, ArsenalConfig.get().spells.frostShards.cooldown);
			
			EntityFrostShard shard = new EntityFrostShard(caster.getEntityWorld(), caster);
			shard.setLocationAndAngles(caster.posX, caster.posY+caster.getEyeHeight(), caster.posZ, 0, 0);
			shard.shoot(caster, caster.rotationPitch, caster.rotationYaw, 0.0f, 1.5f, 0.1f);
			
			caster.getEntityWorld().spawnEntity(shard);
		} else {
			this.caster = null;
		}
	}

	@Override
	public int tick() {
		
		
		return 0;
	}

}
