package com.elytradev.marsinal.capability.impl;

import java.util.function.ObjIntConsumer;

import com.elytradev.marsinal.capability.IMagicResources;

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
