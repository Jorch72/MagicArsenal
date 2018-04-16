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

import java.util.EnumSet;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;

public class SpellDamageSource extends EntityDamageSource {
	private EnumSet<EnumElement> elements = EnumSet.noneOf(EnumElement.class);
	private String spell;
	
	public SpellDamageSource(Entity caster, String spell, EnumElement... elements) {
		super( "spell."+spell, caster );
		//Enforced loosely, but explained comprehensively.
		if(elements.length < 1) throw new IllegalArgumentException("Spell damage must have at least one 'classical' element and one 'governing' element.");
		
		for(EnumElement element : elements) {
			this.elements.add(element);
			if (element==EnumElement.FIRE) this.setFireDamage();
		}
		this.spell = spell;
	}
	
	/** Always returns true. Spell damage type is always magic damage. */
	@Override
	public boolean isMagicDamage() {
		return true;
	}
	
	/** Always returns true. This is a spell, and always deals damage through armor.*/
	@Override
	public boolean isUnblockable() {
		return true;
	}
	
	/**
	 * Gets the full set of elements which govern this spell or ability.
	 */
	public EnumSet<EnumElement> getElements() {
		return elements;
	}
	
	public boolean hasElement(EnumElement element) {
		return elements.contains(element);
	}
	
	/**
	 * Gets the name of the spell or ability which caused this damage.
	 */
	public String getSpell() {
		return spell;
	}
}
