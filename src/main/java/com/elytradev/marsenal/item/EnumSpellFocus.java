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

import java.util.Set;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.magic.DrainLifeSpell;
import com.elytradev.marsenal.magic.RecoverySpell;
import com.elytradev.marsenal.magic.OblationSpell;
import com.elytradev.marsenal.magic.HealingWaveSpell;
import com.elytradev.marsenal.magic.HealingCircleSpell;
import com.elytradev.marsenal.magic.DisruptionSpell;
import com.elytradev.marsenal.magic.ISpellEffect;
import com.elytradev.marsenal.capability.IMagicResources;

import com.google.common.base.Throwables;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public enum EnumSpellFocus implements ISpellFocus {
	HEALING_WAVE  (HealingWaveSpell.class, IMagicResources.RESOURCE_STAMINA, true,  false), //uses Stamina to grant health to friendly look-target
	HEALING_CIRCLE(HealingCircleSpell.class,      IMagicResources.RESOURCE_STAMINA, false, false), //uses Stamina to grant regen to nearby friendly targets
	RECOVERY      (RecoverySpell.class,    IMagicResources.RESOURCE_STAMINA, false, false), //uses Stamina to grant health to the caster
	DRAIN_LIFE    (DrainLifeSpell.class,   IMagicResources.RESOURCE_STAMINA, false, true ), //Drains life from hostile look-target to grant health to the caster
	OBLATION      (OblationSpell.class,    IMagicResources.RESOURCE_STAMINA, true,  false), //Drains life from the caster and grants it to friendly look-target
	DISRUPTION    (DisruptionSpell.class,      IMagicResources.RESOURCE_STAMINA, false, true),
	;
	
	private Class<? extends ISpellEffect> effectClass;
	private ResourceLocation applicableResource;
	private boolean showFriendlyHealth;
	private boolean showHostileHealth;
	
	EnumSpellFocus(Class<? extends ISpellEffect> clazz, ResourceLocation applicableResource, boolean showFriendlyHealth, boolean showHostileHealth) {
		effectClass = clazz;
		this.applicableResource = applicableResource;
		this.showFriendlyHealth = showFriendlyHealth;
		this.showHostileHealth = showHostileHealth;
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
	
	/** @nullable */
	public ResourceLocation getApplicableResource() {
		return applicableResource;
	}
	
	/**
	 * Returns true if this spell affects a friendly look-target's HP, so we should show its health as a resource.
	 */
	public boolean shouldShowFriendlyHealth() {
		return showFriendlyHealth;
	}
	
	public boolean shouldShowHostileHealth() {
		return showHostileHealth;
	}
	
	public static EnumSpellFocus fromMeta(int meta) {
		return values()[meta%values().length];
	}

	@Override
	public void addResources(EntityLivingBase caster, ItemStack stack, Set<ResourceLocation> set) {
		if (applicableResource!=null) set.add(applicableResource);
	}

	@Override
	public EnumTarget classify(EntityLivingBase caster, ItemStack stack, Entity target) {
		if (target instanceof EntityMob && showHostileHealth) {
			return ISpellFocus.EnumTarget.HOSTILE;
		} else if (target instanceof EntityLivingBase && showFriendlyHealth) {
			return ISpellFocus.EnumTarget.FRIENDLY;
		}
		
		return ISpellFocus.EnumTarget.NONE;
	}
}
