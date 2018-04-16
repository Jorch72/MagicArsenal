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
import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.capability.IMagicResources;
import com.elytradev.marsenal.network.SpawnParticleEmitterMessage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

/** Dummy spell effect for testing. Can also be used as an ancestor for spells requiring a caster. */
public class SpellEffect implements ISpellEffect {
	
	@Override
	public void activate(EntityLivingBase caster, IMagicResources res) {
	}

	@Override
	public int tick() {
		return 0;
	}

	
	//STATIC UTILITY METHODS
	
	
	public static boolean canActivate(IMagicResources caster) {
		return caster.getGlobalCooldown()<=0;
	}
	
	public static boolean spendStaminaOrFail(IMagicResources caster, int amount) {
		return caster.spend(IMagicResources.RESOURCE_STAMINA, amount, ArsenalConfig.get().resources.maxStamina, true)>0;
	}
	
	/**
	 * Handles the common case of checking the GCD, and if the caster is ready, spending stamina to activate the ability.
	 * If for any reason the caster is unable to activate this ability with the full spell cost, this method returns
	 * false.
	 * @param caster  The player holding the spell focus
	 * @param amount  The amount of stamina required for the spell
	 * @return        True if the player can cast the spell, and the stamina cost was successfully deducted.
	 */
	public static boolean activateWithStamina(EntityLivingBase caster, int amount) {
		if (!caster.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return false;
		IMagicResources res = caster.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
		return
				canActivate(res) &&
				spendStaminaOrFail(res, amount);
	}
	
	public static void activateCooldown(EntityLivingBase caster, int amount) {
		if (!caster.hasCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null)) return;
		IMagicResources res = caster.getCapability(MagicArsenal.CAPABILTIY_MAGIC_RESOURCES, null);
		res.setGlobalCooldown(amount);
	}
	
	public static void spawnEmitter(String key, EntityLivingBase source, Entity target) {
		new SpawnParticleEmitterMessage(key).at(target).from(source).sendToAllWatchingAndSelf(target);
	}
	
	public static void spawnEmitter(String key, TargetData.Single<? extends Entity> targets) {
		new SpawnParticleEmitterMessage(key).with(targets).sendToAllWatchingTarget();
	}
}
