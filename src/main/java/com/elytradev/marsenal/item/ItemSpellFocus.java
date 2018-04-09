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

public class ItemSpellFocus extends ItemSubtyped<EnumSpellFocus> implements IMetaItemModel {
	public ItemSpellFocus() {
		super("spellfocus", EnumSpellFocus.values(), true);
	}
	
	/*
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab!=MagicArsenal.TAB_MARSENAL) return;
		for(EnumSpellFocus focus : EnumSpellFocus.values()) {
			ItemStack item = new ItemStack(this, 1, focus.ordinal());
			items.add(item);
		}
	}

	@Override
	public String[] getModelLocations() {
		String[] result = new String[EnumSpellFocus.values().length];
		for(int i=0; i<EnumSpellFocus.values().length; i++) {
			EnumSpellFocus focus = EnumSpellFocus.values()[i];
			result[i] = "spellfocus."+focus.name().toLowerCase(Locale.ENGLISH);
		}
		return result;
	}*/
}