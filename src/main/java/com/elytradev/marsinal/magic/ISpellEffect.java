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

package com.elytradev.marsinal.magic;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents an effect which can be activated and maintained. Implementors must supply a no-arg constructor; A new
 * instance will be created when the effect is activated, and {@link #activate(EntityLivingBase)} will be called.
 */
public interface ISpellEffect {
	/**
	 * Computes targeting data and anything else that should only happen once at the start of a spell. One-tick effects
	 * should still go into {@link #tick(Object)} and return 0 to end immediately.
	 */
	@SideOnly(Side.SERVER)
	void activate(EntityLivingBase caster);
	
	/**
	 * Runs one tick of this spell effect. The spell will continue until this method returns false, or 
	 * @param data The object returned by {@link #activate(EntityLivingBase)} at the beginning of this spell activation
	 * @return     The number of ticks until the next activation, or 0 if this effect should end immediately.
	 */
	@SideOnly(Side.SERVER)
	int tick();
	
	/** Not sure whether this is a bright idea, but we may be able to simplify spells for users if the spell API if
	 * spells can send activation effect messages to their clientside counterparts. */
	@SideOnly(Side.CLIENT)
	void tickEffect(EntityLivingBase target);
}
