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
import com.elytradev.marsenal.ArsenalConfig.SpellEntry;
import com.elytradev.marsenal.DamageHelper;
import com.elytradev.marsenal.capability.IMagicResources;

import net.minecraft.entity.EntityLivingBase;

public class LitBoltSpell extends InstantSingleTargetSpell {
	private static final EnumElement[] ELEMENTS = {EnumElement.ARCANE, EnumElement.AIR};
	
	@Override
	public String getSpellKey() {
		return "litBolt";
	}

	@Override
	public String getEmitterKey() {
		return "lightning";
	}

	@Override
	public int getRange() {
		return 20;
	}

	@Override
	public SpellEntry getConfig() {
		return ArsenalConfig.get().spells.litBolt;
	}

	@Override
	public void onActivate(EntityLivingBase caster, IMagicResources res) {
		SpellDamageSource damageSource = new SpellDamageSource(target.getCaster(), "litBolt", getElements()).setElectrical();
		DamageHelper.fireSpellDamage(damageSource, target.getTarget(), getConfig().potency);
	}
	
	@Override
	public EnumElement[] getElements() {
		return ELEMENTS;
	}
}
