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

import java.util.List;
import java.util.Locale;

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.StringExtras;
import com.elytradev.marsenal.gui.ContainerCodex;
import com.elytradev.marsenal.gui.EnumGui;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemCodex extends Item {
	//public static int MAX_PAGES = 12; //TODO: Get from res manager
	
	public ItemCodex() {
		this.setRegistryName(new ResourceLocation("magicarsenal","codex"));
		this.setUnlocalizedName("magicarsenal.codex");
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
		this.setHasSubtypes(false);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}
	
	public void updatePages() {
		//MAX_PAGES = ContainerCodex.CODEX_PAGES.length;
		//MAX_PAGES = 12;
	}
	
	@Override
	public EnumRarity getRarity(ItemStack stack) {
		int maxPages = ContainerCodex.CODEX_PAGES.size();
		if (stack.getItemDamage()<maxPages*0.25f) return EnumRarity.COMMON;
		if (stack.getItemDamage()<maxPages*0.50f) return EnumRarity.UNCOMMON;
		if (stack.getItemDamage()<maxPages*0.75f) return EnumRarity.RARE;
		return EnumRarity.EPIC;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
			player.openGui(MagicArsenal.INSTANCE, EnumGui.TOME.ordinal(), world, player.getHeldItem(hand).getItemDamage(), 0, 0);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}
	
	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 1 - stack.getItemDamage()/(double)(ContainerCodex.CODEX_PAGES.size()-1);
		//return 1 - stack.getMetadata()/(double)(MAX_PAGES-1);
	}
	
	@Override
	public int getRGBDurabilityForDisplay(ItemStack stack) {
		return 0xac33de;
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		if (stack.getItemDamage()>=ContainerCodex.CODEX_PAGES.size()) return false;
		//if (stack.getMetadata()>=MAX_PAGES) return false;
		return true;
	}
	
	@Override
	public boolean isDamaged(ItemStack stack) {
		return false;
	}
	
	@Override
	public int getMetadata(ItemStack stack) {
		// TODO Auto-generated method stub
		return 0;
		//return super.getMetadata(stack);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String flavortext = I18n.translateToLocal("tooltip.magicarsenal.codex."+(stack.getRarity().rarityName.toLowerCase(Locale.ROOT)));
		List<String> lines = StringExtras.wrapForTooltips(flavortext);
		for(String s : lines) {
			tooltip.add("§9§o"+s+"§r");
		}
	}
}
