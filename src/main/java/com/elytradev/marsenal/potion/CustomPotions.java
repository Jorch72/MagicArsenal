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

package com.elytradev.marsenal.potion;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;

public class CustomPotions {
	
	public static ItemStack getBottleOf(Potion potion, int duration) {
		return getBottleOf(potion, 1, duration);
	}
	
	public static ItemStack getBottleOf(Potion potion, int amplifier, int duration) {
		ItemStack stack = new ItemStack(Items.POTIONITEM);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		
		tag.setString("Potion", "minecraft:water"); //Primary potion; we want none of this
		
		NBTTagList effectsList = new NBTTagList();
		NBTTagCompound effect = new NBTTagCompound();
		
		effect.setInteger("Id", Potion.getIdFromPotion(potion));
		effect.setInteger("Duration", duration);
		if (amplifier>1) effect.setInteger("Amplifier", amplifier-1);
		
		effectsList.appendTag(effect);
		tag.setTag("CustomPotionEffects", effectsList);
		
		
		NBTTagCompound display = new NBTTagCompound();
		String unlocalizedName = "potion."+potion.getName();
		@SuppressWarnings("deprecation")
		String bottle = net.minecraft.util.text.translation.I18n.translateToLocal(unlocalizedName);
		display.setString("Name", bottle);
		tag.setTag("display", display);
		
		return stack;
	}
	
	public static ItemStack getTippedArrow(Potion potion, int amplifier, int duration) {
		ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
		NBTTagCompound tag = stack.getTagCompound();
		if (tag==null) {
			tag = new NBTTagCompound();
			stack.setTagCompound(tag);
		}
		
		tag.setString("Potion", "minecraft:water"); //Primary potion; we want none of this
		
		NBTTagList effectsList = new NBTTagList();
		NBTTagCompound effect = new NBTTagCompound();
		
		effect.setInteger("Id", Potion.getIdFromPotion(potion));
		effect.setInteger("Duration", duration);
		if (amplifier>1) effect.setInteger("Amplifier", amplifier-1);
		
		effectsList.appendTag(effect);
		tag.setTag("CustomPotionEffects", effectsList);
		
		
		NBTTagCompound display = new NBTTagCompound();
		String unlocalizedName = "tipped_arrow."+potion.getName();
		@SuppressWarnings("deprecation")
		String localizedName = net.minecraft.util.text.translation.I18n.translateToLocal(unlocalizedName);
		display.setString("Name", localizedName);
		tag.setTag("display", display);
		
		return stack;
	}
}
