/*
 * MIT License
 *
 * Copyright (c) 2017 Isaac Ellingson (Falkreon) and contributors
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
	private EnumSet<Element> elements = EnumSet.noneOf(Element.class);
	private String spell;
	
	public SpellDamageSource(Entity caster, String spell, Element... elements) {
		super( spell, caster );
		//Enforced loosely, but explained comprehensively.
		if(elements.length < 1) throw new IllegalArgumentException("Spell damage must have at least one 'classical' element and one 'governing' element.");
		
		for(Element element : elements) {
			this.elements.add(element);
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
	public EnumSet<Element> getElements() {
		return elements;
	}
	
	public boolean hasElement(Element element) {
		return elements.contains(element);
	}
	
	/**
	 * Gets the name of the spell or ability which caused this damage.
	 */
	public String getSpell() {
		return spell;
	}

	/**
	 * Typically a spell has two Elements: One from the first four "classical" elements, and one from the next four
	 * "governing" elements. This includes spells which do not cause damage, and even passive anti-elemental wards.
	 * 
	 * Anti-elemental wards, conversely, tend to only target the classical elements, especially as the convenience and
	 * operational lifetime goes up.
	 */
	public enum Element {
		/** Governs most straightforward, direct damage spells. Should be mitigated by fire resistance or eliminated by wyvern armor */
		FIRE,
		/** Governs most stun/sleep/cc and passive auras */
		FROST,
		/** Governs most poisons and bleeds, and about half of all healing spells */
		NATURE,
		/** Governs most remote or location-based spells */
		AIR,
		
		/** Governs most lifedrain and damage from artificial creatures */
		UNDEATH,
		/** Governs most far-reaching or global-effect spells */
		HOLY,
		/** Governs most spells which exclusively benefit the caster */
		ARCANE,
		/** Governs most uncontrollable effects and unintended spell consequences */
		CHAOS;
	}
}
