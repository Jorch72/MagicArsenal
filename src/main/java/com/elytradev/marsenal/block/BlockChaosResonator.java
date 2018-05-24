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

import com.elytradev.marsenal.StringExtras;
import com.elytradev.marsenal.tile.TileEntityChaosResonator;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockChaosResonator extends BlockSimple implements ITileEntityProvider {
	public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;
	public static final PropertyEnum<EnumMode>   MODE   = PropertyEnum.create("mode", EnumMode.class);
	
	
	public BlockChaosResonator() {
		super("chaosresonator");
		this.setLightOpacity(0);
		this.setResistance(700f);
		this.setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(MODE, EnumMode.BALANCE));
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, MODE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(meta & 0b0011))
				.withProperty(MODE, EnumMode.of(meta));
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		int result = 0;
		
		result |= state.getValue(FACING).getHorizontalIndex();
		result |= state.getValue(MODE).ordinal() << 2;
		
		return result;
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, placer.getAdjustedHorizontalFacing());
	}
	
	@Override
	public void addInformation(ItemStack stack, World player, List<String> tooltip, ITooltipFlag advanced) {
		StringExtras.addInformation("tooltip.magicarsenal.chaosresonator", "", tooltip);
	}
	
	public static enum EnumMode implements IStringSerializable {
		INSERT("insert"),
		EXTRACT("extract"),
		BALANCE("balance"),
		;

		private String name;
		EnumMode(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		public static EnumMode of(int meta) {
			int m = (meta >>> 2) & 0b0011;
			return values()[m % values().length];
		}
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

	@Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityChaosResonator();
	}
}
