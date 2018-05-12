package com.elytradev.marsenal.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class IngredientBrewingRecipe implements IBrewingRecipe {
	private Ingredient inputPotion;
	private Ingredient ingredientItem;
	private ItemStack output;
	
	public IngredientBrewingRecipe(ItemStack output, Item ingredient, ItemStack input) {
		this(output, Ingredient.fromItem(ingredient), new IngredientNBT(input));
	}
	
	public IngredientBrewingRecipe(ItemStack output, Ingredient ingredient, Ingredient input) {
		this.inputPotion = input;
		this.ingredientItem = ingredient;
		this.output = output;
	}
	
	@Override
	public boolean isInput(ItemStack input) {
		System.out.println("IsInput: "+inputPotion+" ?= "+input+" ==> "+inputPotion.apply(input));
		return inputPotion.apply(input);
	}

	@Override
	public boolean isIngredient(ItemStack ingredient) {
		System.out.println("IsIngredient: "+ingredientItem+" ?= "+ingredient+" ==> "+ingredientItem.apply(ingredient));
		return ingredientItem.apply(ingredient);
	}

	@Override
	public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
		if (inputPotion.apply(input)) {
			System.out.println("getOutput: "+ingredient+" + "+input+" ==> "+output);
			return output.copy();
		} else {
			System.out.println("rejecting getOutput: "+ingredient+" + "+input+" ==> EMPTY");
			return ItemStack.EMPTY;
			/*
			ItemStack deadPotion = new ItemStack(Items.POTIONITEM);
			PotionUtils.addPotionToItemStack(deadPotion, PotionTypes.THICK);
			System.out.println("getOutput: "+ingredient+" + "+input+" ==> "+deadPotion);
			return deadPotion;*/
		}
	}

}
