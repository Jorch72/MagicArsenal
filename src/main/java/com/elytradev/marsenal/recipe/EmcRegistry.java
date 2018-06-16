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

package com.elytradev.marsenal.recipe;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

public class EmcRegistry {
	private Map<ResourceLocation, Entry> entries = new HashMap<>();
	
	public EmcRegistry() {}
	
	public boolean contains(IBlockState blockstate) {
		Entry entry = entries.get(blockstate.getBlock().getRegistryName());
		if (entry==null) return false;
		return (entry.map.containsKey(blockstate.getBlock().getMetaFromState(blockstate)));
	}
	
	public int get(IBlockState blockstate) {
		Entry entry = entries.get(blockstate.getBlock().getRegistryName());
		if (entry==null) {
			return 0;
		} else {
			return entry.get(blockstate);
		}
	}
	
	public void register(String block, int emc) {
		register(new ResourceLocation(block), emc);
	}
	
	public void register(String block, int meta, int emc) {
		register(new ResourceLocation(block), meta, emc);
	}
	
	public void register(Block block, int emc) {
		register(block.getRegistryName(), emc);
	}
	
	public void register(ResourceLocation block, int emc) {
		entries.put(block, new Entry(emc));
	}
	
	public void register(ResourceLocation block, int meta, int emc) {
		Entry entry = entries.get(block);
		if (entry==null) {
			entry = new Entry();
			entries.put(block, entry);
		}
		entry.set(meta, emc);
	}
	
	private static class Entry {
		Map<Integer, Integer> map = new HashMap<>(); //OVERKILL. We could really use some Int2IntOpenHashMap here. Or just two arrays that resize together
		
		public Entry() {}
		
		public Entry(int emcForWildcard) {
			map.put(OreDictionary.WILDCARD_VALUE, emcForWildcard);
		}
		
		public void set(int meta, int emc) {
			if (meta==OreDictionary.WILDCARD_VALUE) {
				map.clear();
				map.put(OreDictionary.WILDCARD_VALUE, emc);
			} else {
				map.put(meta, emc);
			}
		}
		
		public int get(IBlockState state) {
			return get(state.getBlock().getMetaFromState(state));
		}
		
		public int get(int meta) {
			Integer wildcard = map.get(OreDictionary.WILDCARD_VALUE);
			if (wildcard!=null) return wildcard;
			Integer specific = map.get(meta);
			return (specific!=null) ? specific : 0;
		}
	}
}
