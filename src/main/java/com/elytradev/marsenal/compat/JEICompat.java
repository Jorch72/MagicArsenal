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

package com.elytradev.marsenal.compat;

import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellFocus;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEICompat implements IModPlugin {
	
	@Override
	public void register(IModRegistry registry) {
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DRAIN_LIFE.ordinal()),
				ItemStack.class,
				"Spell: Drain Life",
				"Elements: Undeath, Nature",
				"",
				"Effect: Steals health from one entity in line of sight, converting some of it into health for the caster.");
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_WAVE.ordinal()),
				ItemStack.class,
				"Spell: Healing Wave",
				"Elements: Holy, Nature",
				"Uses: Stamina",
				"",
				"Effect: Heals one friendly entity in line of sight.");
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_CIRCLE.ordinal()),
				ItemStack.class,
				"Spell: Healing Circle",
				"Elements: Holy, Air",
				"Uses: Stamina",
				"",
				"Effect: Places a luminous sigil at the caster's location. Entities within the area will heal slowly over time.");
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.RECOVERY.ordinal()),
				ItemStack.class,
				"Spell: Recovery",
				"Elements: Arcane, Nature",
				"Uses: Stamina",
				"",
				"Effect: One swig of this ever-filling flask heals the caster over five seconds.");
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.OBLATION.ordinal()),
				ItemStack.class,
				"Spell: Oblation",
				"Elements: Chaos, Nature",
				"Uses: Stamina",
				"",
				"Effect: Drains a large amount of the caster's health, channeling all of it to one friendly target in line of sight.");
	}
}
