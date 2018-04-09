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

import java.util.ArrayList;
import java.util.Locale;

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/** Mostly used for inert ingredients */
public class ItemSubtyped<T extends Enum<T>> extends Item implements IMetaItemModel {
	
	private Enum<T>[] enumValues;
	private String id;
	private boolean glowing;
	
	public ItemSubtyped(String id, T[] subtypes, boolean glowing) {
		this.setRegistryName(id);
		this.setUnlocalizedName("magicarsenal."+id);
		this.id = id;
		enumValues = subtypes;
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setMaxStackSize(64);
		this.glowing = glowing;
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		int meta = stack.getItemDamage() % enumValues.length;
		return "item.magicarsenal."+id+"."+enumValues[meta].name().toLowerCase();
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list) {
		if (tab.equals(this.getCreativeTab())) {
			for(int i=0; i<enumValues.length; i++) {
				list.add(new ItemStack(this, 1, i));
			}
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		if (glowing) return true;
		return (super.hasEffect(stack));
	}

	@Override
	public String[] getModelLocations() {
		ArrayList<String> variants = new ArrayList<String>();
		for(Enum<T> t : enumValues) {
			if (t instanceof IStringSerializable) {
				variants.add(id + "." + ((IStringSerializable)t).getName() );
			} else {
				variants.add(id + "." + t.name().toLowerCase(Locale.ENGLISH));
			}
		}
		return variants.toArray(new String[variants.size()]);
		
	}
}

