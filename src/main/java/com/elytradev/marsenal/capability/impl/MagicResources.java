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

package com.elytradev.marsenal.capability.impl;

import java.util.function.ObjIntConsumer;

import com.elytradev.marsenal.capability.IMagicResources;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.ResourceLocation;

public class MagicResources implements IMagicResources {
	private Object2IntMap<ResourceLocation> data = new Object2IntOpenHashMap<>();
	private int maxGcd = 0;
	private int gcd = 0;
	
	@Override
	public int getResource(ResourceLocation id, int defaultAmount) {
		return data.getOrDefault(id, defaultAmount);
	}

	@Override
	public int spend(ResourceLocation id, int amount, int defaultAmount, boolean requireAmount) {
		int before = getResource(id, defaultAmount);
		if (requireAmount && before<amount) return 0;
		int after = Math.max(0, before - amount);
		int spent = before-after;
		set(id, after);
		return spent;
	}

	@Override
	public void set(ResourceLocation id, int amount) {
		data.put(id, amount);
	}

	@Override
	public int getMaxCooldown() {
		return maxGcd;
	}

	@Override
	public void setMaxCooldown(int max) {
		maxGcd = max;
	}

	@Override
	public int getGlobalCooldown() {
		return gcd;
	}

	@Override
	public void setGlobalCooldown(int ticks) {
		if (gcd>=ticks) return;
		gcd = ticks;
		maxGcd = ticks;
	}

	@Override
	public void reduceGlobalCooldown(int ticks) {
		gcd -= ticks;
		if (gcd<0) gcd = 0;
	}

	public void forEach(ObjIntConsumer<ResourceLocation> consumer) {
		for(Object2IntMap.Entry<ResourceLocation> entry : data.object2IntEntrySet()) {
			consumer.accept(entry.getKey(), entry.getIntValue());
		}
	}
}
