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

import com.elytradev.marsenal.compat.DracEvoCompat;
import com.elytradev.marsenal.magic.EnumElement;
import com.elytradev.marsenal.magic.SpellDamageSource;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.Loader;

public class DamageHelper {
	
	/**
	 * Fires off spell damage. Returns the amount of damage actually dealt.
	 */
	public static float fireSpellDamage(EntityLivingBase caster, EntityLivingBase target, String spell, float amount, EnumElement... elements) {
		if (target.isDead) return 0f;
		
		SpellDamageSource damageSource = new SpellDamageSource(caster, spell, elements);
		
		SpellEvent.DamageEntity event = new SpellEvent.DamageEntity(spell, caster, target, elements)
				.setDamage(amount);
		if (event.isCanceled()) return 0f;
		
		if (damageSource.hasElement(EnumElement.CHAOS)) {
			//ENGAGE PARANOIA
			
			if (Loader.isModLoaded("draconicevolution")) {
				//ENGAGE MORE PARANOIA
				DracEvoCompat.deployCountermeasures(target);
			}
		}
		
		return (target.attackEntityFrom(damageSource, event.getDamage())) ? event.getDamage() : 0f;
	}
}
