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

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CodexRecipe extends ShapelessRecipes {
	private static final ResourceLocation NAME = new ResourceLocation("magicarsenal","growcodex");
	
	public CodexRecipe() {
		super("magicarsenal.codex", new ItemStack(ArsenalItems.CODEX, 1, 1),
				
				NonNullList.from(Ingredient.EMPTY, 
						Ingredient.fromItem(ArsenalItems.CODEX),
						Ingredient.fromStacks(new ItemStack(ArsenalItems.INGREDIENT, 1, EnumIngredient.PORTAL_SEARED_TOME.ordinal()))
				)
		);
		this.setRegistryName(NAME);
	}

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		boolean codexFound = false;
		int tomeStrength = 0;
		
		for(int y=0; y<inv.getHeight(); y++) {
			for(int x=0; x<inv.getWidth(); x++) {
				ItemStack cur = inv.getStackInRowAndColumn(y, x);
				if (cur.isEmpty()) continue;
				Item item = cur.getItem();
				if (item==ArsenalItems.CODEX) {
					if (codexFound) return false;  //Two codices are invalid
					codexFound = true;
					continue;
				}
				
				if (item==ArsenalItems.INGREDIENT && cur.getMetadata()==EnumIngredient.PORTAL_SEARED_TOME.ordinal()) {
					tomeStrength++;
					continue;
				}
				
				//If we fall down to here a non-codex, non-tome item is present.
				return false;
			}
		}
		
		return codexFound && tomeStrength>0;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		boolean codexFound = false;
		int codexStrength = 0;
		int tomeStrength = 0;
		
		for(int y=0; y<inv.getHeight(); y++) {
			for(int x=0; x<inv.getWidth(); x++) {
				ItemStack cur = inv.getStackInRowAndColumn(y, x);
				if (cur.isEmpty()) continue;
				Item item = cur.getItem();
				if (item==ArsenalItems.CODEX) {
					if (codexFound) return ItemStack.EMPTY;  //Two codices are invalid
					codexFound = true;
					codexStrength = cur.getMetadata();
					continue;
				}
				
				if (item==ArsenalItems.INGREDIENT && cur.getMetadata()==EnumIngredient.PORTAL_SEARED_TOME.ordinal()) {
					tomeStrength++;
					continue;
				}
				
				//If we fall down to here a non-codex, non-tome item is present.
				return ItemStack.EMPTY;
			}
		}
		int combinedStrength = codexStrength+tomeStrength;
		if (tomeStrength==0) return ItemStack.EMPTY;
		if (combinedStrength<ItemCodex.MAX_PAGES) { //Consider allowing more
			return new ItemStack(ArsenalItems.CODEX, 1, combinedStrength);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean canFit(int width, int height) {
		return width*height>=2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ArsenalItems.CODEX, 1, 1);
	}
	
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> stacks = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		
		boolean codexFound = false;
		int codexStrength = 0;
		int tomeStrength = 0;
		
		for(int y=0; y<inv.getHeight(); y++) {
			for(int x=0; x<inv.getWidth(); x++) {
				int index = y + x*inv.getWidth();
				ItemStack cur = inv.getStackInRowAndColumn(y, x);
				if (cur.isEmpty()) {
					stacks.set(index, ItemStack.EMPTY);
					continue;
				}
				Item item = cur.getItem();
				if (item==ArsenalItems.CODEX) {
					if (codexFound) {
						stacks.set(index, cur); //Don't mangle additional codices. We shouldn't need this but YOU NEVER KNOW :/
					} else {
						stacks.set(index, ItemStack.EMPTY);
						if (codexFound)
							codexFound = true;
						codexStrength = cur.getMetadata();
					}
					continue;
				}
				
				if (item==ArsenalItems.INGREDIENT && cur.getMetadata()==EnumIngredient.PORTAL_SEARED_TOME.ordinal()) {
					if (codexStrength+tomeStrength+1>=ItemCodex.MAX_PAGES) {
						stacks.set(index, cur);
					} else {
						tomeStrength++;
						stacks.set(index, ItemStack.EMPTY);
					}
					continue;
				}
				
				//Extraneous items are present. Leave them on the grid
				stacks.set(index, cur);
			}
		}
		
		return stacks;
	}
}
