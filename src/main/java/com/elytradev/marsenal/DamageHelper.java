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

package com.elytradev.marsenal;

import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.SpellDamageSource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;

public class DamageHelper {
	public static final Accessor<DataParameter<Boolean>> CREEPER_POWERED = Accessors.findField(EntityCreeper.class, "field_184714_b", "POWERED", "b");
	
	/**
	 * Fires off spell damage. Returns the amount of damage actually dealt.
	 */
	public static float fireSpellDamage(SpellDamageSource damageSource, EntityLivingBase target, float amount) {
		if (target.isDead || !isAttackable(damageSource.getCaster(), target)) return 0f;
		
		if (damageSource.isElectrical() && target.isWet()) {
			amount = amount * 1.5f;
		}
		
		SpellEvent.DamageEntity event = new SpellEvent.DamageEntity(damageSource, target).setDamage(amount);
		if (event.isCanceled()) return 0f;
		
		//Countermeasures moved to separate mod: Glass Jaw
		
		if (damageSource.isElectrical() && target instanceof EntityCreeper) {
			((EntityCreeper)target).getDataManager().set(CREEPER_POWERED.get(null), true);
		}
		
		return (target.attackEntityFrom(damageSource, event.getDamage())) ? event.getDamage() : 0f;
	}
	
	public static float fireSpellDamage(EntityLivingBase caster, EntityLivingBase target, String spell, float amount, EnumElement... elements) {
		SpellDamageSource damageSource = new SpellDamageSource(caster, spell, elements);
		return fireSpellDamage(damageSource, target, amount);
	}
	
	public static final boolean isAttackable(EntityLivingBase caster, EntityLivingBase target) {
		if (!target.canBeHitWithPotion()) return false;
		if (caster.isOnSameTeam(target)) return false;
		
		return true;
	}
}
