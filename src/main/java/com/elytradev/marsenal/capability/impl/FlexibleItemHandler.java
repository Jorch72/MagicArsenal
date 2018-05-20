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

package com.elytradev.marsenal.capability.impl;

import java.util.function.Predicate;

import com.elytradev.concrete.inventory.ConcreteItemStorage;

import net.minecraft.item.ItemStack;

public class FlexibleItemHandler extends ConcreteItemStorage {
	//private int[] limits;
	private int maxStackSize;
	
	public FlexibleItemHandler(int slots) {
		super(slots);
	}
	
	public FlexibleItemHandler setName(String name) {
		super.withName(name);
		return this;
	}
	
	public FlexibleItemHandler setCanExtract(boolean... extracts) {
		for(int i=0; i<extracts.length; i++) {
			super.setCanExtract(i, extracts[i]);
		}
		return this;
	}
	
	/*
	public FlexibleItemHandler setMaxStackSize(int... maxStackSizes) {
		limits = maxStackSizes;
		return this;
	}*/
	
	public FlexibleItemHandler setMaxStackSize(int maxStackSize) {
		this.maxStackSize = maxStackSize;
		return this;
	}
	/*
	@Override
	public int getSlotLimit(int slot) {
		if (slot>=0 && slot<limits.length) return limits[slot];
		return super.getSlotLimit(slot);
	}*/
	
	@Override
	public int getSlotLimit(int slot) {
		return maxStackSize;
	}
	
	public int getSlotLimit() {
		return maxStackSize;
	}
	
	@SafeVarargs
	public final FlexibleItemHandler setValidators(Predicate<ItemStack>... validators) {
		super.withValidators(validators);
		return this;
	}
}
