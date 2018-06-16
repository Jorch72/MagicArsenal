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

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ItemPotionSigil extends Item implements IBeaconSigil {
	protected Potion potion;
	
	protected int amplifier = 0;
	
	protected float hue = 0;
	protected float saturation = 0;
	protected float value = 0;
	
	public ItemPotionSigil(String id, Potion potion, int amplifier) {
		int rgb = potion.getLiquidColor();
		float r = ((rgb >> 16) & 0xFF) / 255f;
		float g = ((rgb >>  8) & 0xFF) / 255f;
		float b = ((rgb      ) & 0xFF) / 255f;
		
		float alpha = 0.5f*(2*r - g - b);
		float beta = ((float)Math.sqrt(3)/2f) * (g - b);
		hue = (float)Math.atan2(beta, alpha) * (360f / ((float)Math.PI*2f));
		
		float max = Math.max(r, Math.max(g, b));
		float min = Math.min(r, Math.min(g, b));
		float chroma = max-min;
		
		value = max;
		
		saturation = 0;
		if (value>0) saturation = chroma/value;
		
		this.potion = potion;
		this.amplifier = amplifier;
		//this.effect = new PotionEffect(potion, 20*15, amplifier, true, false);
		
		this.setRegistryName(new ResourceLocation("magicarsenal:sigil."+id));
		this.setUnlocalizedName("magicarsenal.sigil."+id);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
		this.setMaxStackSize(1);
	}
	
	@Override
	public float getColorHue() {
		return hue;
	}
	
	@Override
	public void applyEffect(Entity entity, ItemStack stack, BlockPos beacon) {
		if (entity instanceof EntityLivingBase) {
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(potion, 20*15, amplifier, true, false));
		}
	}
}
