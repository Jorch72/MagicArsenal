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

package com.elytradev.marsenal.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;

public class PotionInfuseLife extends PotionBase {
	public static final DamageSource DAMAGE_INFUSE_LIFE = new DamageSource("magicarsenal.infuselife");
	
	public PotionInfuseLife() {
		super(false, 0xFF74aa00);
		
		setTexture("magicarsenal:textures/effects/infuselife.png");
		setPotionName("effect.magicarsenal.infuselife");
		setRegistryName("magicarsenal", "infuselife");
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier) {
		float toHeal = 1;
		
		if (entity.isEntityUndead()) {
			entity.attackEntityFrom(DAMAGE_INFUSE_LIFE, toHeal);
		} else {
			entity.heal(toHeal);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		int ticks = 25 >> (amplifier+1);
		
		if (ticks > 0) {
			return duration % ticks == 0;
		} else {
			return true;
		}
	}
}
