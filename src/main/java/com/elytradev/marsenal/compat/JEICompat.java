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

import java.util.ArrayList;
import java.util.Locale;

import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.item.ArsenalItems;
import com.elytradev.marsenal.item.EnumSpellFocus;
import com.elytradev.marsenal.magic.EnumElement;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
@JEIPlugin
public class JEICompat implements IModPlugin {
	
	@Override
	public void register(IModRegistry registry) {
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DRAIN_LIFE.ordinal()),
				ItemStack.class,
				spellInfo(
						"drainLife",
						EnumElement.UNDEATH, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_WAVE.ordinal()),
				ItemStack.class,
				spellInfo(
						"healingWave",
						EnumElement.HOLY, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.HEALING_CIRCLE.ordinal()),
				ItemStack.class,
				spellInfo(
						"healingCircle",
						EnumElement.HOLY, EnumElement.AIR,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.RECOVERY.ordinal()),
				ItemStack.class,
				spellInfo(
						"recovery",
						EnumElement.ARCANE, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.OBLATION.ordinal()),
				ItemStack.class,
				spellInfo(
						"oblation",
						EnumElement.CHAOS, EnumElement.NATURE,
						IMagicResources.RESOURCE_STAMINA
				));
		
		registry.addIngredientInfo(
				new ItemStack(ArsenalItems.SPELL_FOCUS, 1, EnumSpellFocus.DISRUPTION.ordinal()),
				ItemStack.class,
				spellInfo(
						"disruption",
						EnumElement.ARCANE, EnumElement.FIRE,
						IMagicResources.RESOURCE_STAMINA
				));
	}
	
	private static String[] spellInfo(String spell, EnumElement elem, EnumElement elem2, ResourceLocation resource) {
		ArrayList<String> result = new ArrayList<>();
		result.add(format("info.magicarsenal.label.spell", local("spell.magicarsenal."+spell)));
		result.add(format("info.magicarsenal.label.elements", element(elem), element(elem2)));
		if (resource!=null) {
			result.add(format("info.magicarsenal.label.resource", resource(resource)));
		}
		
		String effectText = local("spell.magicarsenal."+spell+".desc");
		if (effectText!=null && !effectText.equals("spell.magicarsenal."+spell+".desc")) {
			result.add("");
			result.add(format("info.magicarsenal.label.effect", effectText));
		}
		
		return result.toArray(new String[result.size()]);
	}
	
	private static String local(String key) {
		return I18n.translateToLocal(key);
	}
	
	private static String format(String key, Object... args) {
		return I18n.translateToLocalFormatted(key, args);
	}
	
	private static String element(EnumElement elem) {
		return local("info.magicarsenal.element."+ elem.name().toLowerCase(Locale.ENGLISH));
	}
	
	private static String resource(ResourceLocation resource) {
		String domain = resource.getResourceDomain();
		String resourceName = resource.getResourcePath();
		return local("resource."+domain+"."+resourceName);
	}
}
