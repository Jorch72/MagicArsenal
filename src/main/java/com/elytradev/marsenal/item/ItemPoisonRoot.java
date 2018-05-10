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

import com.elytradev.marsenal.MagicArsenal;
import com.elytradev.marsenal.StringExtras;
import com.elytradev.marsenal.block.EnumPoisonPlant;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("deprecation")
public class ItemPoisonRoot extends ItemSeeds {
	//private Block[] varieties = {
	//	ArsenalBlocks.CROP_WOLFSBANE	
	//};
	
	public ItemPoisonRoot(String id, Block crop, Block soil) {
		super(crop, soil);
		this.setRegistryName("root."+id);
		this.setUnlocalizedName("magicarsenal.root."+id);
		this.setCreativeTab(MagicArsenal.TAB_MARSENAL);
		//super("root", EnumPoisonPlant.values(), false);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add("§e"+I18n.translateToLocal("tooltip.magicarsenal.poisonroot."+EnumPoisonPlant.valueOf(stack.getMetadata()).getName()+".taxonomy")+"§r");
		String flavortext = I18n.translateToLocal("tooltip.magicarsenal.poisonroot."+EnumPoisonPlant.valueOf(stack.getMetadata()).getName());
		List<String> lines = StringExtras.WordWrap(flavortext, 35);
		for(String s : lines) {
			tooltip.add("§9§o"+s+"§r");
		}
	}
	/*
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY,  float hitZ) {
		if (ArsenalBlocks.CROP_WOLFSBANE.canPlaceBlockAt(world, pos.offset(facing))) {
			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}*/

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	//@Override
	//public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
	//	return ArsenalBlocks.CROP_WOLFSBANE.getDefaultState();
	//}
}
