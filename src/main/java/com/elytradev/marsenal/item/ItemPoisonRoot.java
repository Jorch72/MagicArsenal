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
