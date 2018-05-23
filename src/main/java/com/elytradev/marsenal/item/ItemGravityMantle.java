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

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(modid="baubles", iface="baubles.api.IBauble")
public class ItemGravityMantle extends Item implements IBauble {

	public ItemGravityMantle() {
		this.setRegistryName("spellbauble.gravitymantle");
		this.setUnlocalizedName("magicarsenal.gravitymantle");
		this.setHasSubtypes(false);
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
	}

	@Override
	@Optional.Method(modid="baubles")
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.BODY;
	}
	
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return armorType==EntityEquipmentSlot.CHEST;
	}
	
	public void grantGravityControl(EntityLivingBase player, ItemStack stack) {
		if (player.getEntityWorld().isRemote) return;
		
		if (player.getEntityWorld().getTotalWorldTime() % 80L == 0L) {
			player.addPotionEffect(new PotionEffect(ArsenalItems.POTION_GRAVITYCONTROL, 20*10, 0, true, false));
		}
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		grantGravityControl(player, stack);
	}
	
	
	@Override
	@Optional.Method(modid="baubles")
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		grantGravityControl(player, itemstack);
	}
}
