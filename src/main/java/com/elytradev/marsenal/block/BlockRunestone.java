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

import java.util.List;

import com.elytradev.marsenal.MagicArsenal;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockRunestone extends Block implements IItemVariants, ITooltip {
	EnumRuneCarving[] varieties;
	public static final PropertyEnum<EnumRuneCarving> CARVING = PropertyEnum.create("carving", EnumRuneCarving.class);
	
	public BlockRunestone(String subId, EnumRuneCarving... varieties) {
		super(Material.ROCK);
		this.setUnlocalizedName("magicarsenal.runestone");
		this.setRegistryName("runestone."+subId);
		this.varieties = varieties;
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
		this.setHardness(4.0f);
		this.setHarvestLevel("pickaxe", 1);
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, CARVING);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(CARVING, varieties[meta % varieties.length]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		EnumRuneCarving carving = state.getValue(CARVING);
		for(int i=0; i<varieties.length; i++) {
			if (carving==varieties[i]) return i;
		}
		
		return 0;
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab!=this.getCreativeTabToDisplayOn()) return;
		
		for(int i=0; i<varieties.length; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getStateFromMeta(meta);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}
	
	@Override
	public String getVariantFromItem(ItemStack stack) {
		IBlockState equivalentState = getStateFromMeta(stack.getMetadata());
		return("carving="+equivalentState.getValue(CARVING).getName());
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flags) {
		EnumRuneCarving carving = getStateFromMeta(stack.getMetadata()).getValue(CARVING);
		String localizedCarving = I18n.translateToLocal("tooltip.magicarsenal.rune."+carving.getName());
		String carvingDesc = I18n.translateToLocalFormatted("tooltip.magicarsenal.carvedwith", localizedCarving);
		tooltip.add(carvingDesc);
	}
}
