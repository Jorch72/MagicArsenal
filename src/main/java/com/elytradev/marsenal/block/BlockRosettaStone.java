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

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRosettaStone extends BlockSimple {
	public static PropertyEnum<RosettaPattern> VARIANT = PropertyEnum.create("variant", RosettaPattern.class);
	
	public BlockRosettaStone() {
		super("rosettastone");
		this.setDefaultState(blockState.getBaseState().withProperty(VARIANT, RosettaPattern.STANDARD));
		this.setLightLevel(1.0f);
	}
	
	@Override
	public float getEnchantPowerBonus(World world, BlockPos pos) {
		return 6;
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(VARIANT, RosettaPattern.values()[meta % RosettaPattern.values().length]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
	
	@Override
	public String getVariantFromItem(ItemStack stack) {
		return "variant="+getStateFromMeta(stack.getMetadata()).getValue(VARIANT).getName();
	}

	public static enum RosettaPattern implements IStringSerializable {
		STANDARD("standard");
		
		private final String name;
		RosettaPattern(String name) {
			this.name = name;
		}
		@Override
		public String getName() { return this.name; }
	}
}
