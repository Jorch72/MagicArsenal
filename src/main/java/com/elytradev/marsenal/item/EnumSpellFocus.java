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

package com.elytradev.marsenal.item;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.magic.DrainLifeSpell;
import com.elytradev.marsenal.magic.ISpellEffect;
import com.elytradev.marsenal.magic.SpellEffect;

import com.google.common.base.Throwables;

public enum EnumSpellFocus {
	HEALING_WAVE(SpellEffect.class),   //uses Stamina to grant health to friendly look-target
	HEALING_CIRCLE(SpellEffect.class), //uses Stamina to grant regen to nearby friendly targets
	RECOVERY(SpellEffect.class),       //uses Stamina to grant health to the caster
	DRAIN_LIFE(DrainLifeSpell.class),     //Drains life from hostile look-target to grant health to nearby friendly targets
	OBLATION(SpellEffect.class),       //Drains life from the caster and grants it to friendly look-target
	;
	
	private Class<? extends ISpellEffect> effectClass;
	
	EnumSpellFocus(Class<? extends ISpellEffect> clazz) {
		effectClass = clazz;
	}
	
	public ISpellEffect createEffect() {
		try {
			return effectClass.getDeclaredConstructor().newInstance();
		} catch (Throwable t) {
			MagicArsenal.LOG.warn("Couldn't create the instance.");
			//Throwables::propagate is deprecated and scheduled for removal for some stupid reason
			Throwables.throwIfUnchecked(t);
			throw new RuntimeException(t);
		}
	}
	
	public static EnumSpellFocus fromMeta(int meta) {
		return values()[meta%values().length];
	}
}
