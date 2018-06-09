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

import java.util.ArrayList;
import java.util.List;

import com.elytradev.concrete.recipe.ItemIngredient;
import com.elytradev.concrete.recipe.impl.ItemStackIngredient;
import com.elytradev.concrete.recipe.impl.OreItemIngredient;
import com.elytradev.marsenal.item.LooseItemIngredient;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid="jei", iface="mezz.jei.api.recipe.IRecipeWrapper")
public class ShapelessAltarRecipe implements IRecipeWrapper {
	private ItemIngredient[] ingredients;
	private ItemStack output;
	private int radiance;
	private int emc;
	
	public ShapelessAltarRecipe(ItemStack output, int radiance, int emc, ItemIngredient... ingredients) {
		this.output = output;
		this.ingredients = ingredients;
		this.radiance = radiance;
		this.emc = emc;
	}
	
	@Override
	@Optional.Method(modid="jei")
	public void getIngredients(IIngredients ingredients) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for(ItemIngredient ingredient : this.ingredients) {
			if (ingredient==null) continue;
			List<ItemStack> items = new ArrayList<>();
			if (ingredient instanceof ItemStackIngredient) {
				items.add(((ItemStackIngredient)ingredient).getItem());
			} else if (ingredient instanceof LooseItemIngredient) {
				items.add(((LooseItemIngredient)ingredient).getItem());
			} else if (ingredient instanceof OreItemIngredient) {
				String key = ((OreItemIngredient)ingredient).getKey();
				if (!OreDictionary.doesOreNameExist(key)) continue;
				items.addAll(OreDictionary.getOres(key));
			} else {
				continue; //Not sure how to handle this
			}
			inputs.add(items);
		}
		
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, output);
	}

	public ItemIngredient[] getIngredients() {
		return ingredients;
	}

	public ItemStack getOutput() {
		return output.copy();
	}

	public boolean canCraftFrom(IItemHandler inventory) {
		/*
		List<ItemIngredient> needed = new ArrayList<>();
		for(ItemIngredient it : ingredients) needed.add(it);
		int searchSlots = Math.min(inventory.getSlots(), 6);
		
		for(int i=0; i<searchSlots; i++) {
			ItemIngredient matched = null;
			for(ItemIngredient ingredient : needed) {
				if (ingredient.apply(inventory.getStackInSlot(i))) {
					matched = ingredient;
					break;
				}
			}
			if (matched==null) return false;
			needed.remove(matched);
			if (needed.isEmpty()) return true;
		}
		
		return needed.isEmpty();*/
		return consumeIngredients(inventory, true);
	}

	public boolean consumeIngredients(IItemHandler inventory, boolean simulate) {
		List<ItemIngredient> needed = new ArrayList<>();
		for(ItemIngredient it : ingredients) needed.add(it);
		int searchSlots = Math.min(inventory.getSlots(), 6);
		
		for(int i=0; i<searchSlots; i++) {
			ItemIngredient matched = null;
			for(ItemIngredient ingredient : needed) {
				if (ingredient.apply(inventory.getStackInSlot(i))) {
					matched = ingredient;
					break;
				}
			}
			if (matched==null) return false;
			needed.remove(matched);
			ItemStack stack = inventory.extractItem(i, 1, simulate);
			if (stack==null || stack.isEmpty()) return false;
			if (needed.isEmpty()) return true;
		}
		
		return needed.isEmpty();
	}

	public int getEMC() {
		return emc;
	}
	
	public int getRadiance() {
		return radiance;
	}
}
