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

package com.elytradev.marsenal.block;

import com.elytradev.marsenal.item.ArsenalItems;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.IStringSerializable;

public enum EnumPoisonPlant implements IStringSerializable {
	WOLFSBANE("wolfsbane"),
	NIGHTSHADE("nightshade")
	;
	
	private String name;
	private Potion potion;
	
	EnumPoisonPlant(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public static EnumPoisonPlant valueOf(int i) {
		return values()[i%values().length];
	}
	
	public ItemStack getRoot() {
		if (this==WOLFSBANE) return new ItemStack(ArsenalItems.ROOT_WOLFSBANE);
		if (this==NIGHTSHADE) return new ItemStack(ArsenalItems.ROOT_NIGHTSHADE);
		return new ItemStack(ArsenalItems.ROOT_WOLFSBANE); //BLEH
	}
	
	public void registerPotion(Potion p) { this.potion = p; }
	
	public Potion getPotion() {
		return potion;
	}
	/*
	public ItemStack getVial() {
		return getVial(20*30, 0);
	}
	
	public ItemStack getVial(int amplifier) {
		return getVial(20*30, amplifier);
	}
	
	public ItemStack getVial(int duration, int amplifier) {
		ItemStack result = new ItemStack(ArsenalItems.POISON_VIAL, 1, this.ordinal());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("Potion", potion.getRegistryName().toString());
		tag.setInteger("Duration", duration);
		tag.setInteger("Amplifier", 0);
		result.setTagCompound(tag);
		
		return result;
	}*/
}
